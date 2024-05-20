package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.PopupConfirmBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class ConfirmDialog(val title: String, val action: String) : BaseBindingDialogFragment<PopupConfirmBinding>() {

    lateinit var confirm: (pkg: String) ->Unit
    var pkg: String? = null

    override val layoutId: Int
        get() = R.layout.popup_confirm

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
    }

    private fun setUp() {
        binding.tvTitle.text = title
        binding.btnOK.text = action
        binding.btnOK.setOnSafeClick {
            confirm.invoke(pkg!!)
            dismiss()
        }
        binding.btnCancel.setOnSafeClick {
            dismiss()
        }
    }

}