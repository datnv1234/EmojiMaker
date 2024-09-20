package com.wa.ai.emojimaker.ui.component.emojimerge.result

import android.Manifest
import android.animation.Animator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.Constant.INTERNAL_MY_CREATIVE_DIR
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.data.model.CollectionUI
import com.wa.ai.emojimaker.databinding.ActivityMergeResultBinding
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.functions.EmojiMixer
import com.wa.ai.emojimaker.functions.Utils
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.ui.dialog.AddToPackageDialog
import com.wa.ai.emojimaker.ui.dialog.CreatePackageDialog
import com.wa.ai.emojimaker.ui.dialog.SaveSuccessDialog
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.AdsConsentManager
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.checkInternetConnection
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.invisible
import com.wa.ai.emojimaker.utils.extention.setFullScreen
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible
import timber.log.Timber
import java.net.URL
import java.util.Date
import java.util.Objects
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow

class MergeResultAct : BaseBindingActivity<ActivityMergeResultBinding, MergeResultVM>() {

    private val isAdsInitializeCalled = AtomicBoolean(false)
    private var interstitialAd: InterstitialAd? = null
    private var adsConsentManager: AdsConsentManager? = null
    private var analytics: FirebaseAnalytics? = null

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_MERGE_EMOJI)

    private var emote1 = ""
    private var emote2 = ""
    private var date = ""
    private var emotResult = ""

    private val mAddToPackageDialog: AddToPackageDialog by lazy {
        AddToPackageDialog(this).apply {
            save = {
                if (it == null) {
                    toast(getString(R.string.please_input_package_name))
                } else {

                    this@MergeResultAct.viewModel.bitmapMutableLiveData.value?.let { it1 ->
                        DeviceUtils.saveToPackage(
                            this@MergeResultAct,
                            INTERNAL_MY_CREATIVE_DIR,
                            packageName = it.id,
                            bitmapImage = it1
                        )
                    }
                    if (!mSaveSuccessDialog.isAdded)
                        mSaveSuccessDialog.show(supportFragmentManager, mSaveSuccessDialog.tag)
                    showInterstitial {}
                }
            }

            createNewPackage = {
                if (!mCreatePackageDialog.isAdded)
                    mCreatePackageDialog.show(supportFragmentManager, mCreatePackageDialog.tag)
            }

        }
    }

    private val mCreatePackageDialog: CreatePackageDialog by lazy {
        CreatePackageDialog(this).apply {
            confirm = {

                this@MergeResultAct.viewModel.bitmapMutableLiveData.value?.let { it1 ->
                    DeviceUtils.saveToPackage(
                        this@MergeResultAct,
                        INTERNAL_MY_CREATIVE_DIR,
                        packageName = it.id,
                        bitmapImage = it1
                    )
                }
                if (!mSaveSuccessDialog.isAdded)
                    mSaveSuccessDialog.show(supportFragmentManager, mSaveSuccessDialog.tag)
                showInterstitial {}
            }
        }
    }

    private val mSaveSuccessDialog: SaveSuccessDialog by lazy {
        SaveSuccessDialog(this).apply {
            home = {
                this@MergeResultAct.finish()
                startActivity(Intent(this@MergeResultAct, MainActivity::class.java))
                showInterstitial {

                }
            }
            createMore = {
                this@MergeResultAct.finish()
                showInterstitial {

                }
            }
        }
    }


    override val layoutId: Int
        get() = R.layout.activity_merge_result

    override fun getViewModel(): Class<MergeResultVM> = MergeResultVM::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        setFullScreen()
        initAdsManager()
        initView()
        initAction()
    }

    private fun initView() {
        kotlin.runCatching {
            intent?.let {
                emote1 = it.getStringExtra(Constant.KEY_EMOTE_1) ?: ""
                emote2 = it.getStringExtra(Constant.KEY_EMOTE_2) ?: ""
                Timber.e("datnv: emote1 $emote1")
                Timber.e("datnv: emote2 $emote2")
                date = it.getStringExtra(Constant.KEY_DATE) ?: ""
            }
            mixEmojis(
                emote1, emote2,
                Objects.requireNonNull<Any?>(date).toString()
            )
        }.onFailure {
            it.printStackTrace()
        }
    }


    override fun setupData() {
        loadNativeAd()
    }

    private fun initAction() {

        binding.btnDownload.setOnSafeClick {
            kotlin.runCatching {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    kotlin.runCatching {
                        Utils.saveImage(
                            binding.imMergeResult,
                            this@MergeResultAct,
                            "\uD83D\uDE22",
                            false
                        )
                    }.onFailure {
                        it.printStackTrace()
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(
                            this@MergeResultAct,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        kotlin.runCatching {
                            Utils.saveImage(
                                binding.imMergeResult,
                                this@MergeResultAct,
                                "\uD83D\uDE22",
                                false
                            )
                        }.onFailure {
                            it.printStackTrace()
                        }
                    } else {
                        kotlin.runCatching {
                            ActivityCompat.requestPermissions(
                                this@MergeResultAct,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1
                            )
                        }.onFailure {
                            it.printStackTrace()
                        }
                    }
                }
                showInterstitial {}
            }.onFailure {
                it.printStackTrace()
            }
        }

        binding.btnShare.setOnSafeClick {
            kotlin.runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    kotlin.runCatching {
                        Utils.saveAndShareImage(
                            binding.imMergeResult,
                            this@MergeResultAct,
                            "\uD83D\uDE22",
                            false
                        )
                    }.onFailure {
                        it.printStackTrace()
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(
                            this@MergeResultAct,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        kotlin.runCatching {
                            Utils.saveAndShareImage(
                                binding.imMergeResult,
                                this@MergeResultAct,
                                "\uD83D\uDE22",
                                false
                            )
                        }.onFailure {
                            it.printStackTrace()
                        }
                    } else {
                        kotlin.runCatching {
                            ActivityCompat.requestPermissions(
                                this@MergeResultAct,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1
                            )
                        }.onFailure {
                            it.printStackTrace()
                        }
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }

        binding.imHome.setOnClickListener {
            kotlin.runCatching {
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(this)
                }
                showInterstitial {
                    finish()
                }
            }.onFailure {
                it.printStackTrace()
            }
        }

        binding.btnAddToPackage.setOnSafeClick {
            if (!mAddToPackageDialog.isAdded)
                mAddToPackageDialog.show(supportFragmentManager, mAddToPackageDialog.tag)
            showInterstitial {}
        }

        binding.btnTryAgain.setOnSafeClick {
            this@MergeResultAct.finish()
        }
    }

    private fun mixEmojis(emoji1: String, emoji2: String, date: String) {
        kotlin.runCatching {
            val em = EmojiMixer(emoji1, emoji2, date, this, object : EmojiMixer.EmojiListener {
                override fun onSuccess(emojiUrl: String) {
                    kotlin.runCatching {
                        emotResult = emojiUrl
                        Timber.e("datnv: emojiUrl $emojiUrl")
                        viewModel.insertEmotToCollection(
                            CollectionUI(path = emojiUrl, path1 = emote1, path2 = emote2)
                        )
                        val thread = Thread {
                            try {
                                val inputStream = URL(emojiUrl).openStream()
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                viewModel.setBitmap(bitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        thread.start()
                        mSaveSuccessDialog.emojiUrl = emojiUrl
                        Glide.with(this@MergeResultAct)
                            .load(emojiUrl)
                            .into(binding.imMergeResult)
                        binding.animationView2.visible()
                        binding.animationView2.playAnimation()
                        binding.animationView2.addAnimatorListener(object :
                            Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                binding.imMergeResult.visible()
                                binding.animationView1.visible()
                                binding.animationView2.invisible()
                            }

                            override fun onAnimationCancel(animation: Animator) {
                            }

                            override fun onAnimationRepeat(animation: Animator) {
                            }
                        })
                    }.onFailure {
                        it.printStackTrace()
                    }
                }

                override fun onFailure(failureReason: String) {
                    binding.viewSuccess.gone()
                    binding.viewFailed.visible()
                }
            })
            val thread = Thread(em)
            thread.start()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun initAdsManager() {
        var isInitializeMobileAdsSdk = false
        adsConsentManager = AdsConsentManager.getInstance(applicationContext)
        adsConsentManager?.gatherConsent(this) { consentError ->
            if (consentError != null || adsConsentManager?.canRequestAds == true) {
                initializeMobileAdsSdk()
                isInitializeMobileAdsSdk = true
            }
        }

        if (!isInitializeMobileAdsSdk && adsConsentManager?.canRequestAds == true) {
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

    private fun showInterstitial(onAdDismissedAction: () -> Unit) {
        if (!checkInternetConnection()) {
            onAdDismissedAction.invoke()
            return
        }

        val timeSubtraction =
            Date().time - SharedPreferenceHelper.getLong(Constant.TIME_LOAD_NEW_INTER_ADS)
        val timeLoad = FirebaseRemoteConfig.getInstance()
            .getLong(RemoteConfigKey.INTER_DELAY)
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
            val keyAdInterAllPrice =
                FirebaseRemoteConfig.getInstance()
                    .getString(RemoteConfigKey.KEY_ADS_INTER_MERGE_EMOJI)
            loadInterAdsMain(keyAdInterAllPrice)

        }
    }

    private var retryAttempt = 0.0
    private fun loadInterAdsMain(keyAdInter: String) {
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
                        Handler(mainLooper).postDelayed({ loadAd() }, delayMillis)
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


    private fun loadNativeAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MERGE_EMOJI)
        ) {
            loadNativeAds(keyNative)
        } else {
            binding.rlNative.visibility = View.GONE
        }
    }

    private fun loadNativeAds(keyAds: String) {
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                this,
                keyAds, { nativeAds ->
                    if (nativeAds != null) {
                        binding.rlNative.visible()
                        val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                        NativeAdsUtils.instance.populateNativeAdVideoView(
                            nativeAds,
                            adNativeVideoBinding.root as NativeAdView
                        )
                        binding.frNativeAds.removeAllViews()
                        binding.frNativeAds.addView(adNativeVideoBinding.root)
                    }
                },
                {

                }
            )
        }
    }
}