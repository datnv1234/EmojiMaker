package com.wa.ai.emojimaker.ui.component.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.wa.ai.emojimaker.common.Constant.ADS
import com.wa.ai.emojimaker.databinding.ActivitySplashBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.component.multilang.MultiLangActivity
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.extention.setStatusBarColor
import timber.log.Timber
import java.util.Date

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {

    val bundle = Bundle()
    private var mInterstitialAd: InterstitialAd? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private val keyAdInter =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_INTER_SPLASH)
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
    }

    private val countDownTimer: CountDownTimer = object : CountDownTimer(15000, 5000) {
        override fun onTick(millisUntilFinished: Long) {
            if (mInterstitialAd == null)
                setUpLoadInterAds()
            else {
                cancel()
                openChooseLanguageActivity()
                showInterstitial()
                finish()
            }
        }

        override fun onFinish() {
            openChooseLanguageActivity()
        }
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
        setUpLoadInterAds()
        countDownTimer.start()
    }

    override fun setupData() {
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
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

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Timber.tag(ADS).d("onAdFailedToShowFullScreenContent: ${loadAdError.message}")
                    Timber.tag(ADS).d("onAdFailedToShowFullScreenContent: ${loadAdError.cause}")
                    Timber.tag(ADS).d("onAdFailedToShowFullScreenContent: ${loadAdError.code}")
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mFirebaseAnalytics?.logEvent("d_load_inter_splash", null)

                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue ->
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                interstitialAd.responseInfo.loadedAdapterResponseInfo

                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros.toDouble() / 1000000.0
                            adRevenue.setRevenue(
                                revenue,
                                adValue.currencyCode
                            )
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)
                            val analytics = FirebaseAnalytics.getInstance(applicationContext)
                            val params = Bundle()
                            params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "admob mediation")
                            params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                            params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                            params.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                            params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            analytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)
                        }
                }
            })

    }

    private fun showInterstitial() {
        // Show inter then handle action
        mInterstitialAd?.fullScreenContentCallback =
            object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    App.adTimeStamp = Date().time
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Timber.tag(ADS).d("onAdFailedToShowFullScreenContent: $adError")
                }
            }
        mInterstitialAd?.show(this@SplashActivity)

        mFirebaseAnalytics?.logEvent("v_inter_ads_splash", null)
    }

    private fun setUpLoadInterAds() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_SPLASH)
        ) {
            loadInterAdsSplash(keyAdInter)
        }
    }
}