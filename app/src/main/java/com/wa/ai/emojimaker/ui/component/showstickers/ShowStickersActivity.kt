package com.wa.ai.emojimaker.ui.component.showstickers

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.View
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.App
import com.wa.ai.emojimaker.BuildConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.Constant.EXTRA_STICKER_PACK_AUTHORITY
import com.wa.ai.emojimaker.common.Constant.EXTRA_STICKER_PACK_ID
import com.wa.ai.emojimaker.common.Constant.EXTRA_STICKER_PACK_NAME
import com.wa.ai.emojimaker.databinding.ActivityShowStickersBinding
import com.wa.ai.emojimaker.ui.adapter.MadeStickerAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.AppUtils.saveSticker
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.copyFileToCache
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.BannerUtils
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import timber.log.Timber
import java.io.File
import java.util.Date


class ShowStickersActivity : BaseBindingActivity<ActivityShowStickersBinding, ShowStickerViewModel>() {

    private var isLoadNativeDone = false
    private var keyInter = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_INTER_SHOW_STICKERS)
//    private var keyInter = "ca-app-pub-3940256099942544/1033173712"
    private val keyNative = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_SHOW_STICKERS)
    private val keyBanner = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_BANNER_SHOW_STICKERS)
//    private val keyBanner = "ca-app-pub-3940256099942544/2014213617"
    private val interDelay = FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.INTER_DELAY)
    private val bannerReload = FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)

    private var mInterstitialAd: InterstitialAd? = null
    private var analytics: FirebaseAnalytics? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null

    private val cateStickerAdapter : MadeStickerAdapter by lazy {
        MadeStickerAdapter(itemClick = {
            //toast("Clicked")
        })
    }

    private val madeStickerAdapter : MadeStickerAdapter by lazy {
        MadeStickerAdapter(itemClick = {
            toast("Clicked")
        })
    }

    override val layoutId: Int
        get() = R.layout.activity_show_stickers

    override fun getViewModel(): Class<ShowStickerViewModel> = ShowStickerViewModel::class.java

    @SuppressLint("NotifyDataSetChanged")
    override fun setupView(savedInstanceState: Bundle?) {

        binding.btnBack.setOnSafeClick {
            finish()
        }
        val isCreative = intent.getBooleanExtra("local", false)
        val category = intent.getStringExtra("category") ?: ""
        val categoryName = intent.getStringExtra("category_name")

        binding.tvTitle.text = categoryName
        binding.btnAddToWhatsapp.setOnSafeClick {
            val intent = Intent().apply {
                action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
                putExtra(EXTRA_STICKER_PACK_ID, "1")
                putExtra(EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY)
                putExtra(EXTRA_STICKER_PACK_NAME, "Alpi Powers")
            }
            try {
                startActivityForResult(intent, 200)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        if (category != null) {
            if (!isCreative) {
                viewModel.getStickers(this, category)
                viewModel.stickersMutableLiveData.observe(this) {
                    cateStickerAdapter.submitList(it.toMutableList())
                    cateStickerAdapter.notifyDataSetChanged()
                }

                binding.rvStickers.adapter = cateStickerAdapter
                binding.btnAddToTelegram.setOnSafeClick {
                    addStickerInCategoryToTele(category)
                    mFirebaseAnalytics?.logEvent("v_inter_ads_add_telegram_category", null)
                }
                binding.btnDownload.setOnSafeClick {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        if (AppUtils.checkPermission(this)) {
                            AppUtils.requestPermissionAndContinue(this)
                            return@setOnSafeClick
                        }
                    }
                    nextAction {
                        downloadStickerInCategory(category)
                    }
                    mFirebaseAnalytics?.logEvent("v_inter_ads_download_category", null)

                }
                binding.btnShare.setOnSafeClick {
                    nextAction {
                        shareStickerInCategory(category)
                    }
                    mFirebaseAnalytics?.logEvent("v_inter_ads_share_category", null)
                }
            } else {
                viewModel.getCreativeSticker(this, category)
                viewModel.localStickerMutableLiveData.observe(this) {
                    madeStickerAdapter.submitList(it.toMutableList())
                    madeStickerAdapter.notifyDataSetChanged()
                }

                binding.rvStickers.adapter = madeStickerAdapter
                binding.btnAddToTelegram.setOnSafeClick {
                    addCreativeStickerToTelegram(category)
                    mFirebaseAnalytics?.logEvent("v_inter_ads_add_telegram_creative", null)
                }

                binding.btnDownload.setOnSafeClick {
                    if (AppUtils.checkPermission(this)) {
                        AppUtils.requestPermissionAndContinue(this)
                        return@setOnSafeClick
                    }
                    nextAction {
                        downloadCreativeSticker(category)
                    }
                    mFirebaseAnalytics?.logEvent("v_inter_ads_download_creative", null)
                }
                binding.btnShare.setOnSafeClick {
                    nextAction {
                        shareCreativeSticker(category)
                    }
                    mFirebaseAnalytics?.logEvent("v_inter_ads_share_creative", null)
                }
            }
        }
    }

    override fun setupData() {

    }

    override fun onStart() {
        super.onStart()
        loadAds()
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun loadAds() {
        val isShowBanner = FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_SHOW_STICKERS)
        if (!isShowBanner) {
            binding.rlBanner.gone()
        } else {
            loadBanner()
        }
        viewModel.loadBanner.observe(this) {
            loadBanner()
        }
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_SHOW_STICKERS)) {
            loadInterAds()
        }
    }

    private fun performImageDownload(imageUrl: Uri?) {
        val request = DownloadManager.Request(imageUrl)
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )
        request.setTitle("Download") // Set a title for the download notification
        request.setDescription("Downloading image...") // Set a description for the download notification
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES,
            "AIEmojiMaker/" + System.currentTimeMillis() + ".png"
        )
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    private fun shareStickerInCategory(category: String) {
        getStickerUriInCategory(category)
        if (viewModel.stickerUri.size != 0)
            AppUtils.shareMultipleImages(this, viewModel.stickerUri.toList())
    }

    private fun addStickerInCategoryToTele(category: String) {
        getStickerUriInCategory(category)
        if (viewModel.stickerUri.size != 0)
            AppUtils.doImport(this, viewModel.stickerUri)
    }

    private fun downloadStickerInCategory(category: String) {
        val assetManager = this.assets
        val listFile = assetManager.list("categories/$category")
        if (listFile != null) {                    //package's size > 0
            for (file in listFile) {
                val inputStream1 = assetManager.open("categories/$category/$file")

                saveSticker(this, AppUtils.convertFileToBitmap(FileUtils.copyAssetFileToCache(
                    this,
                    inputStream1,
                    file
                )), category)
                inputStream1.close()
            }
            toast(getString(R.string.download_done))
        } else {
            toast(getString(R.string.download_failed))
        }
    }

    private fun addCreativeStickerToTelegram(category: String) {
        getCreativeStickerUri(category)
        if (viewModel.stickerUri.size != 0)
            AppUtils.doImport(this, viewModel.stickerUri)
    }

    private fun downloadCreativeSticker(category: String) {
        val cw = ContextWrapper(this)
        val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
        val files = directory.listFiles()      // Get packages
        if (files != null) {                    //package's size > 0
            for (file in files) {
                if (file.isDirectory && file.name.equals(category)) {
                    val stickers = file.listFiles()
                    if (stickers != null) {
                        for (sticker in stickers) {
                            saveSticker(this, AppUtils.convertFileToBitmap(sticker), category)
                        }
                    }
                    break
                }
            }
            toast(getString(R.string.download_done))
        } else {
            toast(getString(R.string.download_failed))
        }
    }

    private fun shareCreativeSticker(category: String) {
        getCreativeStickerUri(category)
        if (viewModel.stickerUri.size != 0)
            AppUtils.shareMultipleImages(this, viewModel.stickerUri.toList())
    }

    private fun getStickerUriInCategory(category: String) {
        viewModel.stickerUri.clear()
        val assetManager = this.assets
        val listFile = assetManager.list("categories/$category")
        if (listFile != null) {                    //package's size > 0
            for (file in listFile) {
                val inputStream1 = assetManager.open("categories/$category/$file")
                viewModel.stickerUri.add(
                    FileUtils.getUriForFile(
                        this,
                        FileUtils.copyAssetFileToCache(
                            this,
                            inputStream1,
                            file
                        )
                    )
                )
                inputStream1.close()
            }
        }
    }

    private fun getCreativeStickerUri(category: String) {
        viewModel.stickerUri.clear()
        val cw = ContextWrapper(this)
        val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
        val files = directory.listFiles()      // Get packages
        if (files != null) {                    //package's size > 0
            for (file in files) {
                if (file.isDirectory && file.name.equals(category)) {
                    val stickers = file.listFiles()
                    if (stickers != null) {
                        for (sticker in stickers) {
                            viewModel.stickerUri.add(
                                FileUtils.getUriForFile(this, copyFileToCache(this, sticker))
                            )
                        }
                    }
                    break
                }
            }
        }
    }

    private fun loadBanner() {
        viewModel.starTimeCountReloadBanner(bannerReload)
        BannerUtils.instance?.loadCollapsibleBanner(this, keyBanner)
    }

    private fun nextAction(action:() -> Unit) {
        val isShowAd = (Date().time - App.adTimeStamp) > interDelay
        if (isShowAd && mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback =
                object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        App.adTimeStamp = Date().time
                        action()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        action()
                    }
                }
            mInterstitialAd?.show(this@ShowStickersActivity)
            loadInterAds()
        }else{
            action()
        }
    }

    //Ads
    var loadInterCount = 0
    private fun loadInterAds() {
        InterstitialAd.load(
            this,
            keyInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    Timber.tag(Constant.ADS).d("onAdFailedToShowFullScreenContent: ${loadAdError.message}")
                    Timber.tag(Constant.ADS).d("onAdFailedToShowFullScreenContent: ${loadAdError.cause}")
                    if (loadInterCount < 3) {
                        loadInterAds()
                        loadInterCount++
                    }
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    loadInterCount = 0
                    mFirebaseAnalytics?.logEvent("d_load_inter", null)

                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue -> // Lấy thông tin về nhà cung cấp quảng cáo
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                interstitialAd.responseInfo.loadedAdapterResponseInfo
                            // Gửi thông tin doanh thu quảng cáo đến Adjust
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
                            analytics?.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)
                        }
                }
            })
    }
}