package com.wa.ai.emojimaker.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivitySplashBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.intro.IntroActivity
import com.wa.ai.emojimaker.ui.main.MainActivity
import com.wa.ai.emojimaker.ui.multilang.MultiLangActivity
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.extention.isGrantNotificationPermission
import com.wa.ai.emojimaker.utils.extention.setStatusBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {

    val bundle = Bundle()
    private var mInterstitialAd: InterstitialAd? = null
    private var analytics: FirebaseAnalytics? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun onStart() {
        super.onStart()
        setUpLoadInterAds()
    }
    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
        viewModel.fetchTokenRemoteConfig()
        binding.imgLaunch.postDelayed(
            {
                openNextScreen()
            }, 5000
        )
    }

    private fun openNextScreen() {
        if (mInterstitialAd != null) {
            // Nếu quảng cáo đã tải xong, hiển thị quảng cáo và chuyển đến Activity mới sau khi quảng cáo kết thúc
            mInterstitialAd?.fullScreenContentCallback =
                object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        openMainActivity()
                        finish()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        openMainActivity()
                        finish()
                    }
                }
            mInterstitialAd?.show(this@SplashActivity)

            mFirebaseAnalytics?.logEvent("v_inter_ads_splash", null)
        }else{
            openMainActivity()
            finish()
        }

    }

    override fun setupData() {
        viewModel.typeNextScreen.observe(this) { type ->
            with(type) {
                when (this) {
                    Constant.TYPE_SHOW_LANGUAGE_ACT -> {
                        Intent(this@SplashActivity, MultiLangActivity::class.java).apply {
                            putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SPLASH)
                            startActivity(this)
                        }
                    }

                    Constant.TYPE_SHOW_INTRO_ACT -> {
                        startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                    }

                    Constant.TYPE_SHOW_PERMISSION -> {
                        val isGrantNotification = isGrantNotificationPermission()
                        val isNextScreen =
                            SharedPreferenceHelper.getBoolean(Constant.KEY_CLICK_GO, false)
                        if (isGrantNotification) {
                            lifecycleScope.launch(Dispatchers.IO) {

                                withContext(Dispatchers.Main) {
                                    if (isNextScreen) {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                MainActivity::class.java
                                                //PermissionActivity::class.java
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            if (isNextScreen) {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            } else {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            }
                        }
                    }

                    else -> {
                        startActivity(
                            Intent(this@SplashActivity, MainActivity::class.java)
                        )
                    }
                }
                finish()
            }
        }
    }

    private fun openMainActivity() {
        viewModel.getTypeNextScreen()
    }

    private fun loadInterAdsSplash(keyAdsInter: String) {

        InterstitialAd.load(
            this@SplashActivity,
            keyAdsInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mFirebaseAnalytics?.logEvent("d_load_inter_splash", null)

                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue -> // Lấy thông tin về nhà cung cấp quảng cáo
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                interstitialAd.responseInfo.loadedAdapterResponseInfo
                            // Gửi thông tin doanh thu quảng cáo đến Adjust
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros.toDouble() / 1000000.0
                            adRevenue.setRevenue(
                                revenue,
                                adValue.currencyCode
                            )
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)
                            analytics = FirebaseAnalytics.getInstance(applicationContext)
                            val params = Bundle()
                            params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "admob mediation")
                            params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                            params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                            params.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                            params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            analytics?.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    mFirebaseAnalytics?.logEvent("e_load_inter_splash", null)

                }
            })

    }

    private fun setUpLoadInterAds() {
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        if (firebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_OPEN_APP)) {
            val adConfig = firebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_INTER_OPEN_APP)
            if (adConfig.isNotEmpty()) {
                loadInterAdsSplash(adConfig)
            } else {
                loadInterAdsSplash(getString(R.string.inter_splash))
            }
        }
    }

    companion object {
        private const val SPLASH_DELAY: Long = 10000
        private const val COUNT_DOWN_INTERVAL: Long = 1000
    }
}