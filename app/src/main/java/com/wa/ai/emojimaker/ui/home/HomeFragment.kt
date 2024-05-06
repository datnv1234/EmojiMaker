package com.wa.ai.emojimaker.ui.home

import android.app.Notification.Action
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.FragmentHomeBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingFragment
import com.wa.ai.emojimaker.ui.emojimaker.EmojiMakerActivity

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {


    override fun getViewModel(): Class<HomeViewModel> = HomeViewModel::class.java
    override fun registerOnBackPress() {
    }

    override val layoutId: Int
        get() = R.layout.fragment_home
    override val title: String
        get() = getString(R.string.app_name)

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        binding.btnCreateSticker.setOnClickListener {
            startActivity(Intent(context, EmojiMakerActivity::class.java))
        }
    }

    override fun setupData() {

    }

}