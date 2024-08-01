package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.nativead.NativeAdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogSaveBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiViewModel
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class SaveStickerDialog : BaseBindingDialogFragment<DialogSaveBinding>() {

    var addToPackage: (() -> Unit)? = null
    var download: ((binding : DialogSaveBinding) -> Unit)? = null
    var share: ((binding : DialogSaveBinding) -> Unit)? = null
    private var adView: NativeAdView? = null

    override val layoutId: Int
        get() = R.layout.dialog_save

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        val emojiViewModel : EmojiViewModel = ViewModelProvider(requireActivity())[EmojiViewModel::class.java]
        emojiViewModel.bitmapMutableLiveData.observe(this) {
            binding.imgPreview.setImageBitmap(it)
        }
        emojiViewModel.nativeAdSaveDialog.observe(this) {
            adView = it
            addNativeAd()
        }
    }

    private fun setUp() {
        isCancelable = false
        binding.btnAddToPackage.setOnSafeClick {
            addToPackage?.invoke()
            dismiss()
        }
        binding.btnDownload.setOnSafeClick {
            download?.invoke(binding)
            dismiss()
        }
        binding.btnShare.setOnSafeClick {
            share?.invoke(binding)
            dismiss()
        }
        binding.btnClose.setOnSafeClick {
            dismiss()
        }
        binding.bg.setOnSafeClick {
            dismiss()
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