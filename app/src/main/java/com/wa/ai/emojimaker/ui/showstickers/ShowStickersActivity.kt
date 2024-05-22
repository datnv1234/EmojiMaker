package com.wa.ai.emojimaker.ui.showstickers

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Movie
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.wa.ai.emojimaker.App
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.Constant.PERMISSION_REQUEST_CODE
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.StickerUri
import com.wa.ai.emojimaker.databinding.ActivityShowStickersBinding
import com.wa.ai.emojimaker.ui.adapter.MadeStickerAdapter
import com.wa.ai.emojimaker.ui.adapter.UriAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.AppUtils.saveSticker
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.copyFileToCache
import com.wa.ai.emojimaker.utils.PermissionApp.requestPermission
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import java.io.File


class ShowStickersActivity : BaseBindingActivity<ActivityShowStickersBinding, ShowStickerViewModel>() {

    private val cateStickerAdapter : MadeStickerAdapter by lazy {
        MadeStickerAdapter(itemClick = {
            toast("Clicked")
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
        val category = intent.getStringExtra("category")
        val categoryName = intent.getStringExtra("category_name")
        val categorySize = intent.getIntExtra("category_size", 0)
        binding.tvTitle.text = categoryName

        if (category != null) {
            if (!isCreative) {
                viewModel.getStickers(this, category, categorySize)

                viewModel.stickersMutableLiveData.observe(this) {
                    cateStickerAdapter.submitList(it.toMutableList())
                    cateStickerAdapter.notifyDataSetChanged()
                }
                binding.rvStickers.adapter = cateStickerAdapter
                binding.btnAddToTelegram.setOnSafeClick {
                    addStickerInCategoryToTele(category)
                }
                binding.btnDownload.setOnSafeClick {
                    if (AppUtils.checkPermission(this)) {
                        AppUtils.requestPermissionAndContinue(this)
                        return@setOnSafeClick
                    }
                    downloadStickerInCategory(category)
                }
                binding.btnShare.setOnSafeClick {
                    shareStickerInCategory(category)
                }
            } else {
                viewModel.getLocalSticker(this, category, categorySize)
                viewModel.localStickerMutableLiveData.observe(this) {
                    madeStickerAdapter.submitList(it.toMutableList())
                    madeStickerAdapter.notifyDataSetChanged()
                }
                binding.rvStickers.adapter = madeStickerAdapter
                binding.btnAddToTelegram.setOnSafeClick {
                    addCreativeStickerToTelegram(category)
                }
                binding.btnDownload.setOnSafeClick {
                    if (AppUtils.checkPermission(this)) {
                        AppUtils.requestPermissionAndContinue(this)
                        return@setOnSafeClick
                    }
                    downloadCreativeSticker(category)
                }
                binding.btnShare.setOnSafeClick {
                    shareCreativeSticker(category)
                }
            }
        }
    }

    override fun setupData() {

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
        val listFile = assetManager.list("categories/$category/")
        if (listFile != null) {                    //package's size > 0
            for (file in listFile) {
                val inputStream1 = assetManager.open("categories/$category/$file")

                saveSticker(this, AppUtils.convertFileToBitmap(FileUtils.copyAssetFileToCache(
                    this,
                    inputStream1,
                    file
                )), category)
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
        val listFile = assetManager.list("categories/$category/")
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
}