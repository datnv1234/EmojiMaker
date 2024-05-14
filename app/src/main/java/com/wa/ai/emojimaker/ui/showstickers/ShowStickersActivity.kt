package com.wa.ai.emojimaker.ui.showstickers

import android.os.Bundle
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.ActivityShowStickersBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity

class ShowStickersActivity : BaseBindingActivity<ActivityShowStickersBinding, ShowStickerViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_show_stickers

    override fun getViewModel(): Class<ShowStickerViewModel> = ShowStickerViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {

    }

    override fun setupData() {
    }
}