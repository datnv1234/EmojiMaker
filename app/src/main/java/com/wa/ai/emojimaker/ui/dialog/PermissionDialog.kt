package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogPermissionBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment

class PermissionDialog : BaseBindingDialogFragment<DialogPermissionBinding>() {
    var clickOkPermission: (() -> Unit)? = null
    override val layoutId: Int
        get() = R.layout.dialog_permission

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
    }

    private fun setUp() {
        isCancelable = false
        binding.btnOkPermission.setOnClickListener { v ->
            clickOkPermission?.invoke()
        }
        binding.btnCancelSetting.setOnClickListener { v -> dismiss() }
    }
}