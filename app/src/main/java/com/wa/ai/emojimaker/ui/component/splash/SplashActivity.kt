package com.wa.ai.emojimaker.ui.component.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.lifecycleScope
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.App
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.databinding.ActivitySplashBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.component.multilang.MultiLangActivity
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.extention.setStatusBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {

    val bundle = Bundle()
    private var mInterstitialAd: InterstitialAd? = null
    private var analytics: FirebaseAnalytics? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
    }
    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
        //viewModel.getOptions(this)
        val countDownTimer: CountDownTimer = object : CountDownTimer(20000, 5000) {
            override fun onTick(millisUntilFinished: Long) {
                if (mInterstitialAd == null)
                    setUpLoadInterAds()
                else if (millisUntilFinished < 13000){
                    cancel()
                    openNextScreen()
                }
            }

            override fun onFinish() {
                openNextScreen()
            }
        }
        countDownTimer.start()

    }

    private fun openNextScreen() {
        if (mInterstitialAd != null) {
            // Show inter then handle action
            mInterstitialAd?.fullScreenContentCallback =
                object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        App.adTimeStamp = Date().time
                        openChooseLanguageActivity()
                        finish()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        openChooseLanguageActivity()
                        finish()
                    }
                }
            mInterstitialAd?.show(this@SplashActivity)

            mFirebaseAnalytics?.logEvent("v_inter_ads_splash", null)
        }else{
            openChooseLanguageActivity()
            finish()
        }

    }

    override fun setupData() {
        setUpLoadInterAds()

    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            getOptions()
        }
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun openChooseLanguageActivity() {
        val intent = Intent(this@SplashActivity, MultiLangActivity::class.java)
        intent.putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SPLASH)
        startActivity(intent)
        finish()
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
        val adConfig = firebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_INTER_SPLASH)
        if (firebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_SPLASH)) {
            loadInterAdsSplash(adConfig)
        }
    }

    private fun getOptions() {
        getFile(Constant.ACCESSORIES)
        getFile(Constant.BEARD)
        getFile(Constant.BROW)
        getFile(Constant.EYES)
        getFile(Constant.FACE)
        getFile(Constant.GLASS)
        getFile(Constant.HAIR)
        getFile(Constant.HAND)
        getFile(Constant.HAT)
        getFile(Constant.MOUTH)
        getFile(Constant.NOSE)
    }
    private fun getFile(category: String) {
        val assetManager = this.assets
        val listFile = assetManager.list("item_options/$category")
        if (listFile != null) {
            for (file in listFile) {
                val inputStream = assetManager.open("item_options/$category/$file")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    DeviceUtils.saveToPackage(
                        this,
                        Constant.INTERNAL_ITEM_OPTIONS_DIR,
                        packageName = category,
                        bitmapImage = bitmap,
                        fileName = file
                    )
                    if (!bitmap.isRecycled) {
                        bitmap.recycle()
                    }
                }
                inputStream.close()
            }
        }
    }
}