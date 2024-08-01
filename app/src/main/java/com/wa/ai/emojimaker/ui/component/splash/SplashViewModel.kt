package com.wa.ai.emojimaker.ui.component.splash

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.ui.base.BaseViewModel
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
            loadNativeHome(context)
            loadNativeDialog(context)
            loadNativeMyCreative(context)
        }
    }

    private val _nativeAdHome: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdHome: LiveData<NativeAdView>
        get() = _nativeAdHome

    private val _nativeAdDialog: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdDialog: LiveData<NativeAdView>
        get() = _nativeAdDialog

    private val _nativeAdMyCreative: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdMyCreative: LiveData<NativeAdView>
        get() = _nativeAdMyCreative

    private fun loadNativeHome(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_HOME)) {
            val keyAd = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)
            val adView = loadNativeAdHome(context = context, keyAd)
            _nativeAdHome.postValue(adView)
        }
    }

    private fun loadNativeMyCreative(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MY_CREATIVE)) {
            val keyAd = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_MY_CREATIVE)
            val adView = loadNativeAd(context = context, keyAd)
            _nativeAdMyCreative.postValue(adView)
        }
    }

    private fun loadNativeDialog(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_SETTINGS)) {
            val keyAd = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_SETTINGS)
            val adView = loadNativeAd(context = context, keyAd)
            _nativeAdDialog.postValue(adView)
        }
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

    private fun loadNativeAd(context: Context, keyAd: String) : NativeAdView {
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
}