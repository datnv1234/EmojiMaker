package com.wa.ai.emojimaker.ui.component.splash

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import androidx.core.view.isEmpty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.ui.component.splash.SplashActivity.Companion.isUseMonet
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {
    private var timer: CountDownTimer? = null
    private var _isCompleteMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isCompleteLiveData: LiveData<Boolean>
        get() = _isCompleteMutableLiveData

    private fun createCountDownTimer(time : Long): CountDownTimer {
        return object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                _isCompleteMutableLiveData.postValue(true)
            }
        }
    }

    fun starTimeCount(time : Long) {
        kotlin.runCatching {
            timer?.cancel()
            timer = createCountDownTimer(time)
            timer?.start()
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun loadAds(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            loadNativeLanguage(context)
            loadNativeIntro(context)
            loadNativeHome(context)
            loadNativeDialog(context)
        }
    }
    private val _nativeAdLanguage: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdLanguage: LiveData<NativeAdView>
        get() = _nativeAdLanguage

    private val _nativeAdIntro: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdIntro: LiveData<NativeAdView>
        get() = _nativeAdIntro

    private val _nativeAdHome: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdHome: LiveData<NativeAdView>
        get() = _nativeAdHome

    private val _nativeAdDialog: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdDialog: LiveData<NativeAdView>
        get() = _nativeAdDialog


    private fun loadNativeDialog(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_HOME)) {
            val keyAdNativeHigh = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME_HIGH)
            val keyAdNativeMedium = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME_MEDIUM)
            val keyAdNativeAllPrice = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)
            val listKeyAds = listOf(keyAdNativeHigh, keyAdNativeMedium, keyAdNativeAllPrice)
            if (isUseMonet) {
                val adView = loadNativeAdDialog(context = context, listKeyAds)
                _nativeAdDialog.postValue(adView)
            } else {
                val adView = loadNativeAdDialog(context = context, keyAdNativeAllPrice)
                _nativeAdDialog.postValue(adView)
            }
        }
    }

    private fun loadNativeHome(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_HOME)) {
            val keyAdNativeHigh = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME_HIGH)
            val keyAdNativeMedium = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME_MEDIUM)
            val keyAdNativeAllPrice = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)
            val listKeyAds = listOf(keyAdNativeHigh, keyAdNativeMedium, keyAdNativeAllPrice)
            if (isUseMonet) {
                val adView = loadNativeAdHome(context = context, listKeyAds)
                _nativeAdHome.postValue(adView)
            } else {
                val adView = loadNativeAdHome(context = context, keyAdNativeAllPrice)
                _nativeAdHome.postValue(adView)
            }
        }
    }

    private fun loadNativeLanguage(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_LANGUAGE)) {
            val keyAdNativeHigh = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_LANGUAGE_HIGH)
            val keyAdNativeMedium = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_LANGUAGE_MEDIUM)
            val keyAdNativeAllPrice = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_LANGUAGE)
            val listKeyAds = listOf(keyAdNativeHigh, keyAdNativeMedium, keyAdNativeAllPrice)
            if (isUseMonet) {
                val adView = loadNativeAdVideo(context = context, listKeyAds)
                _nativeAdLanguage.postValue(adView)
            } else {
                val adView = loadNativeAdVideo(context = context, keyAdNativeAllPrice)
                _nativeAdLanguage.postValue(adView)
            }
        }
    }

    private fun loadNativeIntro(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_INTRO)) {
            val keyAdNativeHigh = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_INTRO_HIGH)
            val keyAdNativeMedium = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_INTRO_MEDIUM)
            val keyAdNativeAllPrice = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_INTRO)
            val listKeyAds = listOf(keyAdNativeHigh, keyAdNativeMedium, keyAdNativeAllPrice)
            if (isUseMonet) {
                val adView = loadNativeAdVideo(context = context, listKeyAds)
                _nativeAdIntro.postValue(adView)
            } else {
                val adView = loadNativeAdVideo(context = context, keyAdNativeAllPrice)
                _nativeAdIntro.postValue(adView)
            }
        }
    }

    private fun loadNativeAdVideo(context: Context, keyAd : String) : NativeAdView {
        val adView = NativeAdView(context)
        NativeAdsUtils.instance.loadNativeAds(
            context,
            keyAd
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_video, adView, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                adView.removeAllViews()
                adView.addView(adLayoutView)
            }
        }
        return adView
    }

    private fun loadNativeAdVideo(context: Context, keyAds : List<String>) : NativeAdView {
        val adView = NativeAdView(context)
        NativeAdsUtils.instance.loadNativeAdsSequence(
            context,
            keyAds
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_video, adView, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                adView.removeAllViews()
                adView.addView(adLayoutView)
            }
        }
        return adView
    }

    private fun loadNativeAdHome(context: Context, keyAd : String) : NativeAdView {
        val adView = NativeAdView(context)
        NativeAdsUtils.instance.loadNativeAds(
            context,
            keyAd
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_content_home, adView, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                adView.removeAllViews()
                adView.addView(adLayoutView)
            }
        }
        return adView
    }

    private fun loadNativeAdHome(context: Context, keyAds : List<String>) : NativeAdView {
        val adView = NativeAdView(context)
        NativeAdsUtils.instance.loadNativeAdsSequence(
            context,
            keyAds
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_content_home, adView, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                adView.removeAllViews()
                adView.addView(adLayoutView)
            }
        }
        return adView
    }

    private fun loadNativeAdDialog(context: Context, keyAd : String) : NativeAdView {
        val adView = NativeAdView(context)
        NativeAdsUtils.instance.loadNativeAds(
            context,
            keyAd
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_content, adView, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                adView.removeAllViews()
                adView.addView(adLayoutView)
            }
        }
        return adView
    }

    private fun loadNativeAdDialog(context: Context, keyAds : List<String>) : NativeAdView {
        val adView = NativeAdView(context)
        NativeAdsUtils.instance.loadNativeAdsSequence(
            context,
            keyAds
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_content, adView, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                adView.removeAllViews()
                adView.addView(adLayoutView)
            }
        }
        return adView
    }
}