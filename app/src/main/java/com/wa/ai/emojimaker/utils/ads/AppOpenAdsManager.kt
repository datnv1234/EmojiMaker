package com.wa.ai.emojimaker.utils.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.utils.DeviceUtils
import java.util.Date



class AppOpenAdManager(context: Context) {

    val TIME_LOAD_NEW_OPEN_ADS = 5000L

    private var googleMobileAdsConsentManager: AdsConsentManager =
        AdsConsentManager.getInstance(context)
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    private val timeNewOpenAds =
        if (DeviceUtils.checkInternetConnection(context)) FirebaseRemoteConfig.getInstance()
            .getLong("time_load_new_open_ads") else TIME_LOAD_NEW_OPEN_ADS


    private val adsID = if (DeviceUtils.checkInternetConnection(context)) FirebaseRemoteConfig.getInstance()
        .getString("open_app_id_ads") else context.getString(R.string.open_app)

    private var loadTime: Long = 0

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adsID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                }
            }
        )
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(timeNewOpenAds)
    }


    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        if (isShowingAd) {
            return
        }

        if (!isAdAvailable()) {
            onShowAdCompleteListener.onShowAdComplete()
            if (googleMobileAdsConsentManager.canRequestAds) {
                loadAd(activity)
            }
            return
        }


        appOpenAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false


                    onShowAdCompleteListener.onShowAdComplete()
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadAd(activity)
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadAd(activity)
                    }
                }

                override fun onAdShowedFullScreenContent() {
                }
            }
        isShowingAd = true
        appOpenAd?.show(activity)
    }
}