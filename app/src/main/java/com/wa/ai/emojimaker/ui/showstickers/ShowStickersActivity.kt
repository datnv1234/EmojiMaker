package com.wa.ai.emojimaker.ui.showstickers

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Movie
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.StickerUri
import com.wa.ai.emojimaker.databinding.ActivityShowStickersBinding
import com.wa.ai.emojimaker.ui.adapter.MadeStickerAdapter
import com.wa.ai.emojimaker.ui.adapter.UriAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.AppUtils
import com.wa.ai.emojimaker.utils.FileUtils
import com.wa.ai.emojimaker.utils.FileUtils.copyFileToCache
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
        val isLocal = intent.getBooleanExtra("local", false)
        val category = intent.getStringExtra("category")
        val categoryName = intent.getStringExtra("category_name")
        val categorySize = intent.getIntExtra("category_size", 0)
        binding.tvTitle.text = categoryName

        if (category != null) {
            if (!isLocal) {
                viewModel.getStickers(this, category, categorySize)

                viewModel.stickersMutableLiveData.observe(this) {
                    cateStickerAdapter.submitList(it.toMutableList())
                    cateStickerAdapter.notifyDataSetChanged()
                }
                binding.rvStickers.adapter = cateStickerAdapter
                Log.d(TAG, "setupView: " + viewModel.stickersMutableLiveData.value?.size)

            } else {
                viewModel.getLocalSticker(this, category, categorySize)
                viewModel.localStickerMutableLiveData.observe(this) {
                    madeStickerAdapter.submitList(it.toMutableList())
                    madeStickerAdapter.notifyDataSetChanged()
                }
                binding.rvStickers.adapter = madeStickerAdapter
                binding.btnAddToTelegram.setOnSafeClick {
                    toast("OKay!")
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

                    AppUtils.doImport(this, viewModel.stickerUri)
                }
            }
        }
    }

    override fun setupData() {

    }
}