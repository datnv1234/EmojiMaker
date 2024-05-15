package com.wa.ai.emojimaker.ui.showstickers

import android.os.Bundle
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.ActivityShowStickersBinding
import com.wa.ai.emojimaker.ui.adapter.UriAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity

class ShowStickersActivity : BaseBindingActivity<ActivityShowStickersBinding, ShowStickerViewModel>() {

    private val uriAdapter : UriAdapter by lazy {
        UriAdapter()
    }
    override val layoutId: Int
        get() = R.layout.activity_show_stickers

    override fun getViewModel(): Class<ShowStickerViewModel> = ShowStickerViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {

    }

    override fun setupData() {
        val category = intent.getStringExtra("category")
        val categoryName = intent.getStringExtra("category_name")

        binding.tvTitle.text = categoryName
        if (category != null) {
            viewModel.getStickers(category)
            viewModel.categoriesMutableLiveData.observe(this) {
                uriAdapter.submitList(it.toMutableList())
            }
            binding.rvSticker.adapter = uriAdapter
        }
    }
}