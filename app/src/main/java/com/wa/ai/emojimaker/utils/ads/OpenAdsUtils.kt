package com.wa.ai.emojimaker.utils.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.wa.ai.emojimaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class openAdsUtils : Application(), Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    private var appOpenAdManager: AppOpenAdManager = AppOpenAdManager()
    private var currentActivity: Activity? = null

    override fun onCreate(owner: LifecycleOwner) {
        super<Application>.onCreate()
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadConfig()
        appOpenAdManager = AppOpenAdManager()
    }


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        currentActivity?.let { appOpenAdManager.showAdIfAvailable(it) }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    fun loadAd(activity: Activity) {
        appOpenAdManager.loadAd(activity)
    }

    interface OnShowAdCompleteListener {
        fun onAdShown()
    }

    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    private fun loadConfig() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build()
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            firebaseRemoteConfig.fetchAndActivate()

        }
    }

    private class AppOpenAdManager {
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false
            private set

        fun loadAd(context: Context) {
            if (isLoadingAd || isAdAvailable()) {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(context, context.getString(R.string.open_app), request, object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    isLoadingAd = false
                    Timber.tag("openAdsUtils").d("There was an error while loading ad ")
                }

                override fun onAdLoaded(openAd: AppOpenAd) {
                    super.onAdLoaded(openAd)
                    appOpenAd = openAd
                    isLoadingAd = false
                    Timber.tag("openAdsUtils").d("Ad loaded ")

                }
            })
        }

        private fun isAdAvailable(): Boolean {
            return appOpenAd != null
        }

        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(activity, object : OnShowAdCompleteListener {
                override fun onAdShown() {

                }
            })
        }

        fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
            if (isShowingAd) {
                return
            }

            if (!isAdAvailable()) {
                onShowAdCompleteListener.onAdShown()
                return
            }

            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    isShowingAd = false
                    onShowAdCompleteListener.onAdShown()
                    appOpenAd = null
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    isShowingAd = false
                    onShowAdCompleteListener.onAdShown()
                    appOpenAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }
            }

            isShowingAd = true
            appOpenAd?.show(activity)
        }
    }
}