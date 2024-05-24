package com.wa.ai.emojimaker.utils.ads

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.wa.ai.emojimaker.App
import com.wa.ai.emojimaker.R
import java.util.Date

class AppOpenAdsManager(private val myApplication: App): ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var currentActivity:Activity? = null
    private var isShowingAds = false
    private var loadTime: Long = 0

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }


    /* Utility method to check if ad was loaded more than n hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val numHours = 4
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /* Check if ad exists and can be shown.  */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    }

    private fun fetchAd(){
        if (isAdAvailable()){
            return
        }
        val loadCallbacks = object  : AppOpenAdLoadCallback(){
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                this@AppOpenAdsManager.appOpenAd = appOpenAd
                loadTime = Date().time
            }
        }
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            myApplication,
            currentActivity!!.getString(R.string.open_app),
            request,
            loadCallbacks
        )
    }

    private fun showAdIfAvailable(){
        if (!isShowingAds && isAdAvailable()){
            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAds = false
                    fetchAd()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAds = true
                }
            }
            appOpenAd!!.show(currentActivity!!)
        }else{
            fetchAd()
        }
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAds){
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (!isShowingAds){
            currentActivity = activity
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        showAdIfAvailable()
    }
}