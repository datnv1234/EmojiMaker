package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogAddToPackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class AddToPackageDialog : BaseBindingDialogFragment<DialogAddToPackageBinding>() {

    override val layoutId: Int
        get() = R.layout.dialog_add_to_package

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setup()
    }

    private fun setup() {
        binding.mainView.setOnSafeClick {
            dismiss()
        }
        binding.btnClose.setOnSafeClick {
            dismiss()
        }
    }
}