package com.wa.ai.emojimaker

import android.app.Application
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.service.receiver.NetworkChangeReceiver
import com.wa.ai.emojimaker.utils.MyDebugTree
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.SystemUtil
import com.wa.ai.emojimaker.utils.ads.AppOpenAdsManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    private var mNetworkReceiver: NetworkChangeReceiver? = null

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    private fun registerNetworkBroadcastForNougat() {
        registerReceiver(
            mNetworkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initLog()
        mNetworkReceiver = NetworkChangeReceiver()
        registerNetworkBroadcastForNougat()

        loadConfig()
        initTrackingAdjust()
    }

    private fun loadConfig() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
                val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(60)
                    .build()
                firebaseRemoteConfig.setConfigSettingsAsync(configSettings).await()
                firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults).await()
                firebaseRemoteConfig.fetchAndActivate().await()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initTrackingAdjust() {
        val appToken = getString(R.string.adjust)
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(this, appToken, environment)
        config.setLogLevel(LogLevel.WARN)
        Adjust.onCreate(config)
    }

    private fun initLog() {
        Timber.plant(MyDebugTree())
    }



    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        SystemUtil.setLocale(this)
    }

    companion object {
        var adTimeStamp: Long = 0L
        var forceUpdate: Boolean = false
        lateinit var instance: App
    }
}