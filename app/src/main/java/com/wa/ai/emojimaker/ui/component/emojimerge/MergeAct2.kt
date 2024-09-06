package com.wa.ai.emojimaker.ui.component.emojimerge

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.MessageEvent
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivityMerge2Binding
import com.wa.ai.emojimaker.ui.adapter.EmojiAdapter1
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.base.observeWithCatch
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.AdsConsentManager
import com.wa.ai.emojimaker.utils.extention.isNetworkAvailable
import com.wa.ai.emojimaker.utils.extention.setFullScreen
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.ui.component.emojimerge.result.MergeResultAct
import com.wa.ai.emojimaker.ui.dialog.DialogInternetConnection
import com.wa.ai.emojimaker.ui.dialog.DialogLoading
import com.wa.ai.emojimaker.utils.ads.BannerUtils
import com.wa.ai.emojimaker.utils.extention.checkInternetConnection
import com.wa.ai.emojimaker.utils.extention.gone
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow

class MergeAct2 : BaseBindingActivity<ActivityMerge2Binding, MergeVM>() {

    private val isAdsInitializeCalled = AtomicBoolean(false)
    private var interstitialAd: InterstitialAd? = null
    private var adsConsentManager: AdsConsentManager? = null
    private var analytics: FirebaseAnalytics? = null

    private val keyAdInterAllPrice = FirebaseRemoteConfig.getInstance()
        .getString(RemoteConfigKey.KEY_ADS_INTER_MERGE_EMOJI)

    private val bannerReload =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)
    private val keyAdBannerAllPrice =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_BANNER_MERGE_EMOJI)

    private var emote1 = ""
    private var emote2 = ""
    private var date1 = ""
    private var date2 = ""

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var animator1: ObjectAnimator
    private lateinit var animator2: ObjectAnimator

    private val dialogInternetConnection: DialogInternetConnection by lazy {
        DialogInternetConnection().apply {
            onClickGotoSetting = {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }

            onCancel = {
                dialogInternetConnection.dismiss()
            }
        }
    }

    private val dialogLoading: DialogLoading by lazy {
        DialogLoading().apply {
            onClickDone = {
                if (emote1.isNotEmpty() && emote2.isNotEmpty()) {
                    kotlin.runCatching {
                        Intent(this@MergeAct2, MergeResultAct::class.java).apply {
                            putExtra(Constant.KEY_EMOTE_1, emote1)
                            putExtra(Constant.KEY_EMOTE_2, emote2)
                            putExtra(Constant.KEY_DATE, listOf(date1, date2).random())
                            startActivity(this)
                        }
                        handler.postDelayed({
                            reset()
                        }, 1000)
                        showInterstitial { }
                    }.onFailure {
                        it.printStackTrace()
                    }
                } else {
                    toast(getString(R.string.you_need_to))
                }
            }
        }
    }

    private val emojiAdapter1: EmojiAdapter1 by lazy {
        EmojiAdapter1().apply {
            callBack = { _, item ->
                if (emote1.isEmpty()) {
                    Glide.with(this@MergeAct2)
                        .load("https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis_png/${item.emojiUnicode}.png")
                        .into(binding.imgElement1)
                    stopAnimation1()
                    startFadeAnimation2()
                    emote1 = item.emojiUnicode
                    date1 = item.date
                } else if (emote2.isEmpty()) {
                    Glide.with(this@MergeAct2)
                        .load("https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis_png/${item.emojiUnicode}.png")
                        .into(binding.imgElement2)
                    stopAnimation2()
                    emote2 = item.emojiUnicode
                    date2 = item.date
                    binding.btnMergeEmoji.isEnabled = true
                }
            }
            binding.rvEmot.adapter = this
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_merge_2

    override fun getViewModel() = MergeVM::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        setFullScreen()
        EventBus.getDefault().register(this)
        initAdsManager()
        animator1 = getAnimator(binding.viewUnselected1)
        animator2 = getAnimator(binding.viewUnselected2)
        initView()
        initAction()
        kotlin.runCatching {
            if (!checkInternetConnection()) {
                if (!dialogInternetConnection.isAdded) {
                    dialogInternetConnection.show(supportFragmentManager, null)
                }
            } else {
                if (dialogInternetConnection.isAdded) {
                    dialogInternetConnection.dismiss()
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun setupData() {
        loadAds()
        viewModel.getAllListEmoji(this)
        viewModel.emojiLiveData.observeWithCatch(this) {
            emojiAdapter1.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initView() {
        kotlin.runCatching {
            startFadeAnimation1()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun initAction() {
        binding.imBack.setOnSafeClick {
            kotlin.runCatching {
                showInterstitial(false) {
                    finish()
                }
            }.onFailure {
                it.printStackTrace()
            }
        }

        binding.btnMergeEmoji.setOnSafeClick {
            kotlin.runCatching {
                if (!dialogLoading.isAdded) {
                    dialogLoading.show(supportFragmentManager, dialogLoading.tag)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }

        binding.btnRefresh.setOnSafeClick {
            showInterstitial {}
            reset()
        }
    }

    private fun getAnimator(view: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = 400 // time duration for the animation
            repeatCount = ValueAnimator.INFINITE // repeat
            repeatMode = ValueAnimator.REVERSE // blink
        }
    }

    private fun reset() {
        emote1 = ""
        emote2 = ""
        date1 = ""
        date2 = ""
        stopAnimation2()
        startFadeAnimation1()
        binding.imgElement1.setImageResource(R.drawable.ic_question1)
        binding.imgElement2.setImageResource(R.drawable.ic_question2)
        binding.btnMergeEmoji.isEnabled = false
    }

    private fun startFadeAnimation1() {
        kotlin.runCatching {
            animator1.start()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun stopAnimation1() {
        kotlin.runCatching {
            animator1.cancel()
            binding.viewUnselected1.alpha = 1f
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun startFadeAnimation2() {
        kotlin.runCatching {
            animator2.start()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun stopAnimation2() {
        kotlin.runCatching {
            animator2.cancel()
            binding.viewUnselected2.alpha = 1f
        }.onFailure {
            it.printStackTrace()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(messageEvent: MessageEvent) {
        kotlin.runCatching {
            when (messageEvent.typeEvent) {
                Constant.EVENT_NET_WORK_CHANGE -> {
                    kotlin.runCatching {
                        if (!checkInternetConnection()) {
                            if (!dialogInternetConnection.isAdded) {
                                dialogInternetConnection.show(supportFragmentManager, null)
                            }
                        } else {
                            if (dialogInternetConnection.isAdded) {
                                dialogInternetConnection.dismiss()
                            }
                        }
                    }.onFailure {
                        it.printStackTrace()
                    }
                }

                else -> {}
            }
        }.onFailure { it.printStackTrace() }
    }

    private fun loadAds() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_MERGE_EMOJI)
        ) {
            loadBanner()
        } else {
            binding.rlBanner.gone()
        }
        viewModel.loadBanner.observe(this) {
            loadBanner()
        }
    }

    private fun loadBanner() {
        viewModel.starTimeCountReloadBanner(bannerReload)
        BannerUtils.instance?.loadCollapsibleBanner(this, keyAdBannerAllPrice) {}

    }

    private fun initAdsManager() {
        adsConsentManager = AdsConsentManager.getInstance(this)
        adsConsentManager?.gatherConsent(this) { consentError ->
            if (consentError != null) {
                Timber.e("datnv: ${consentError.errorCode}: ${consentError.message}")
                initializeMobileAdsSdk()
            }

            if (adsConsentManager?.canRequestAds == true) {
                initializeMobileAdsSdk()
            }
        }

        if (adsConsentManager?.canRequestAds == true) {
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isAdsInitializeCalled.getAndSet(true)) {
            return
        }
        try {
            MobileAds.initialize(this) {}
        } catch (e: Exception) {
            Timber.e(e)
        }
        loadAd()
    }

    private fun showInterstitial(reload: Boolean = true, onAdDismissedAction: () -> Unit) {
        if (!checkInternetConnection()) {
            onAdDismissedAction.invoke()
            return
        }

        val timeLoad = FirebaseRemoteConfig.getInstance()
            .getLong(RemoteConfigKey.INTER_DELAY)

        val timeSubtraction =
            Date().time - SharedPreferenceHelper.getLong(Constant.TIME_LOAD_NEW_INTER_ADS)
        if (timeSubtraction <= timeLoad) {
            onAdDismissedAction.invoke()
            return
        }

        if (interstitialAd == null) {
            if (adsConsentManager?.canRequestAds == false) {
                onAdDismissedAction.invoke()
                return
            }
            onAdDismissedAction.invoke()
            return
        }

        interstitialAd?.show(this)
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Timber.e("datnv: Ad was dismissed. ")
                interstitialAd = null
                SharedPreferenceHelper.storeLong(
                    Constant.TIME_LOAD_NEW_INTER_ADS,
                    Date().time
                )
                if (reload)
                    loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                kotlin.runCatching {
                    onAdDismissedAction.invoke()
                }.onFailure {
                    it.printStackTrace()
                }
                Timber.e("Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                kotlin.runCatching {
                    onAdDismissedAction.invoke()
                }.onFailure {
                    it.printStackTrace()
                }
                Timber.e("datnv: Ad showed fullscreen content. ")
            }
        }
    }

    private fun loadAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_MERGE_EMOJI)
        ) {
            loadAdInter(keyAdInterAllPrice)
        }
    }

    private var retryAttempt = 0.0
    private fun loadAdInter(keyAdInter: String) {
        InterstitialAd.load(
            this,
            keyAdInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.e("datnv: domain: ${adError.domain}, code: ${adError.code},  + message: ${adError.message}")
                    interstitialAd = null
                    retryAttempt++
                    if (retryAttempt < 4) {
                        val delayMillis = TimeUnit.SECONDS.toMillis(
                            2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong()
                        )
                        handler.postDelayed({ loadAd() }, delayMillis)
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Timber.e("datnv: Ad was loaded.")
                    interstitialAd = ad
                    retryAttempt = 0.0
                    interstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue ->
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                interstitialAd?.responseInfo?.loadedAdapterResponseInfo
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
                            analytics?.logEvent("ad_impression_2", params)
                        }
                }
            }
        )
    }
}