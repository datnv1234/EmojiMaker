package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogInternetConnectionBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class DialogInternetConnection : BaseBindingDialogFragment<DialogInternetConnectionBinding>() {

    var onClickGotoSetting: (() -> Unit) = {}
    var onCancel: (() -> Unit) = {}

    override val layoutId: Int
        get() = R.layout.dialog_internet_connection

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.color.dialog_background)
        initAction()
    }

    private fun initAction() {
        binding.tvCancel.setOnSafeClick {
            onCancel()
        }

        binding.tvGoToSetting.setOnSafeClick {
            onClickGotoSetting()
        }
    }
}