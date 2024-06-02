package com.wa.ai.emojimaker.ui.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogSaveSuccessBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class SaveSuccessDialog(val bitmap: Bitmap) : BaseBindingDialogFragment<DialogSaveSuccessBinding>() {

    lateinit var home:() -> Unit
    lateinit var createMore: () -> Unit
    override val layoutId: Int
        get() = R.layout.dialog_save_success

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
    }

    private fun setUp() {
        binding.imgPreview.setImageBitmap(bitmap)
        binding.btnHome.setOnSafeClick {
            home.invoke()
            this@SaveSuccessDialog.dismiss()
        }
        binding.btnCreateMore.setOnSafeClick {
            createMore.invoke()
            this@SaveSuccessDialog.dismiss()
        }
    }

}