package com.wa.ai.emojimaker.ui.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogSaveBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class SaveStickerDialog : BaseBindingDialogFragment<DialogSaveBinding>() {

    var bitmap: Bitmap? = null
    var addToPackage: ((binding : DialogSaveBinding) -> Unit)? = null
    var download: ((binding : DialogSaveBinding) -> Unit)? = null
    var share: ((binding : DialogSaveBinding) -> Unit)? = null

    override val layoutId: Int
        get() = R.layout.dialog_save


    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        if (bitmap != null) {
            binding.imgPreview.setImageBitmap(bitmap)
        }
    }

    private fun setUp() {
        isCancelable = false
        binding.btnAddToPackage.setOnSafeClick {
            addToPackage?.invoke(binding)
        }
        binding.btnDownload.setOnSafeClick {
            download?.invoke(binding)
        }
        binding.btnShare.setOnSafeClick {
            share?.invoke(binding)
        }
        binding.btnClose.setOnSafeClick {
            dismiss()
        }
        binding.bg.setOnSafeClick {
            dismiss()
        }
    }
}