package com.wa.ai.emojimaker.ui.showstickers

import android.annotation.SuppressLint
import android.graphics.Movie
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.StickerUri
import com.wa.ai.emojimaker.databinding.ActivityShowStickersBinding
import com.wa.ai.emojimaker.ui.adapter.UriAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick


class ShowStickersActivity : BaseBindingActivity<ActivityShowStickersBinding, ShowStickerViewModel>() {

    private val uriAdapter : UriAdapter by lazy {
        UriAdapter(itemClick = {
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
        val category = intent.getStringExtra("category")
        val categoryName = intent.getStringExtra("category_name")
        val categorySize = intent.getIntExtra("category_size", 0)
        binding.tvTitle.text = categoryName
        if (category != null) {
            viewModel.getStickers(category, categorySize)

            viewModel.stickersMutableLiveData.observe(this) {
                uriAdapter.submitList(it.toMutableList())
                uriAdapter.notifyDataSetChanged()
            }
            binding.rvStickers.adapter = uriAdapter

        }
    }

    override fun setupData() {

    }
}