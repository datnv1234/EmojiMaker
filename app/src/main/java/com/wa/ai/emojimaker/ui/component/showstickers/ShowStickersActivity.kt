package com.wa.ai.emojimaker.ui.component.showstickers

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import com.adjust.sdk.Adjust
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.databinding.ActivityShowStickersBinding
import com.wa.ai.emojimaker.databinding.AdNativeContentBinding
import com.wa.ai.emojimaker.functions.Utils
import com.wa.ai.emojimaker.ui.adapter.CreateStickerAdapter
import com.wa.ai.emojimaker.ui.adapter.MadeStickerAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.dialog.DeleteStickerDialog
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.copyFileToCache
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.BannerUtils
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.invisible
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible
import java.io.File

class ShowStickersActivity :
    BaseBindingActivity<ActivityShowStickersBinding, ShowStickerViewModel>() {

    private val bannerReload =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_SHOW_STICKERS)

    private val cateStickerAdapter: MadeStickerAdapter by lazy {
        MadeStickerAdapter()
    }

    private val madeStickerAdapter: CreateStickerAdapter by lazy {
        CreateStickerAdapter {
            deletePkgDialog.sticker = it
            binding.btnDelete.visible()
        }
    }

    private val deletePkgDialog: DeleteStickerDialog by lazy {
        DeleteStickerDialog(getString(R.string.delete)).apply {
            confirm = { sticker ->
                sticker.path?.let {
                    DeviceUtils.deleteSticker(it)
                    viewModel.removeSticker(sticker)
                    binding.btnDelete.invisible()
                }
            }
        }
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

        }

        binding.btnDelete.setOnSafeClick {
            if (!deletePkgDialog.isAdded) {
                deletePkgDialog.show(supportFragmentManager, deletePkgDialog.tag)
            }
        }

        if (category != "") {
            if (!isCreative) {
                viewModel.getStickers(this, category)
                viewModel.stickersMutableLiveData.observe(this) {
                    cateStickerAdapter.submitList(it.toMutableList())
                    cateStickerAdapter.notifyDataSetChanged()
                }

                binding.rvStickers.adapter = cateStickerAdapter
                binding.btnAddToTelegram.setOnSafeClick {
                    addStickerInCategoryToTele(category)
                }
                binding.btnDownload.setOnSafeClick {
                    downloadStickerInCategory(category)
                }
                binding.btnShare.setOnSafeClick {
                    shareStickerInCategory(category)
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
                }

                binding.btnDownload.setOnSafeClick {
                    downloadCreativeSticker(category)
                }
                binding.btnShare.setOnSafeClick {
                    shareCreativeSticker(category)
                }
            }
        }
    }

    override fun setupData() {
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
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_SHOW_STICKERS)
        ) {
            loadNativeAds(keyNative)
        } else {
            binding.rlNative.visibility = View.GONE
        }

        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_SHOW_STICKERS)
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
        val keyAdBannerAllPrice = FirebaseRemoteConfig.getInstance()
            .getString(RemoteConfigKey.KEY_ADS_BANNER_SHOW_STICKERS)
        BannerUtils.instance?.loadCollapsibleBanner(this, keyAdBannerAllPrice)
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
                Utils.saveImage(
                    AppUtils.convertFileToBitmap(
                        FileUtils.copyAssetFileToCache(
                            this,
                            inputStream1,
                            file
                        )
                    ), this
                )
                inputStream1.close()
            }
            toast(getString(R.string.saved))
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
                            Utils.saveImage(AppUtils.convertFileToBitmap(sticker), this)
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

    private fun loadNativeAds(keyAds: String) {
        if (!DeviceUtils.checkInternetConnection(this))
            binding.rlNative.gone()
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                applicationContext,
                keyAds
            ) { nativeAds ->
                if (nativeAds != null) {
                    binding.rlNative.visible()
                    val adNativeVideoBinding = AdNativeContentBinding.inflate(layoutInflater)
                    NativeAdsUtils.instance.populateNativeAdVideoView(
                        nativeAds,
                        adNativeVideoBinding.root
                    )
                    binding.frNativeAds.removeAllViews()
                    binding.frNativeAds.addView(adNativeVideoBinding.root)
                }
            }
        }

    }
}