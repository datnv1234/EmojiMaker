package com.wa.ai.emojimaker.ui.emojimaker

import android.os.Bundle
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.ActivityEmojiMakerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity

class EmojiMakerActivity : BaseBindingActivity<ActivityEmojiMakerBinding, EmojiMakerViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_emoji_maker

    override fun getViewModel(): Class<EmojiMakerViewModel> = EmojiMakerViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) { }

    override fun setupData() {

    }
}