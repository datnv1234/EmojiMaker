package com.wa.ai.emojimaker.service

import android.annotation.SuppressLint
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.wa.ai.emojimaker.App
import com.wa.ai.emojimaker.R
import timber.log.Timber
import javax.inject.Inject

class RemoteConfigService @Inject constructor() {

    companion object {
        const val FORCE_UPDATE = "forceUpdate"

        @SuppressLint("StaticFieldLeak")
        private var instanceRemoteConfig: FirebaseRemoteConfig? = null

        fun getIntentRemoteConfig() {
            instanceRemoteConfig ?: synchronized(this) {
                instanceRemoteConfig ?: Firebase.remoteConfig.also { instanceRemoteConfig = it }
            }
        }
    }

    fun getForceUpdateRemoteConfig(): Boolean {
        return App.forceUpdate
    }

    fun fetchAndSaveForceUpdate(): Boolean {
        getIntentRemoteConfig()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }
        instanceRemoteConfig?.setConfigSettingsAsync(configSettings)
        instanceRemoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
        return fetchRemoteConfig()
    }

    private fun saveForceUpdateValue(forceUpdate: Boolean) {
        App.forceUpdate = forceUpdate
    }

    private fun fetchRemoteConfig(): Boolean {
        var token = false
        instanceRemoteConfig?.let { config ->
            token = config[FORCE_UPDATE].asBoolean()
            try {
                instanceRemoteConfig?.fetchAndActivate()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        token = config[FORCE_UPDATE].asBoolean()
                        saveForceUpdateValue(token)
                    }
                }
            } catch (ex: Exception) {
                Timber.e("datnv: fetchRemoteConfig: $ex")
            }
        }
        return token
    }


}
