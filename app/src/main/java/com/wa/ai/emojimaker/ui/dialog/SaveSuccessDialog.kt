package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.nativead.NativeAdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogSaveSuccessBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiViewModel
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class SaveSuccessDialog : BaseBindingDialogFragment<DialogSaveSuccessBinding>() {

    lateinit var home:() -> Unit
    lateinit var createMore: () -> Unit
    private var adView: NativeAdView? = null

    override val layoutId: Int
        get() = R.layout.dialog_save_success

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        val emojiViewModel: EmojiViewModel = ViewModelProvider(requireActivity())[EmojiViewModel::class.java]
        emojiViewModel.bitmapMutableLiveData.observe(this) {
            binding.imgPreview.setImageBitmap(it)
        }
        emojiViewModel.nativeAdSaveSuccessDialog.observe(this) {
            adView = it
            addNativeAd()
        }
    }

    private fun setUp() {
        binding.btnHome.setOnSafeClick {
            home.invoke()
            this@SaveSuccessDialog.dismiss()
        }
        binding.btnCreateMore.setOnSafeClick {
            createMore.invoke()
            this@SaveSuccessDialog.dismiss()
        }
    }

    private fun addNativeAd() {
        binding.rlNative.visible()
        adView?.let {
            val adContainer = binding.frNativeAds
            if (it.parent != null) {
                (it.parent as ViewGroup).removeView(it)
            }
            adContainer.removeAllViews()
            adContainer.addView(it)
        }
    }
}