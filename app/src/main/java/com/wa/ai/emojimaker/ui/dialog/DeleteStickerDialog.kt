package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.DialogDeleteStickerBinding
import com.wa.ai.emojimaker.databinding.PopupConfirmBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class DeleteStickerDialog(val action: String) : BaseBindingDialogFragment<DialogDeleteStickerBinding>() {

    lateinit var confirm: (sticker: MadeStickerModel) ->Unit
    var sticker: MadeStickerModel? = null

    override val layoutId: Int
        get() = R.layout.dialog_delete_sticker

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
    }

    private fun setUp() {
        binding.btnOK.text = action
        binding.btnOK.setOnSafeClick {
            if (sticker != null)
                confirm.invoke(sticker!!)
            dismiss()
        }
        binding.btnCancel.setOnSafeClick {
            dismiss()
        }
        binding.btnClose.setOnSafeClick {
            dismiss()
        }
        binding.tvDes.text = getString(R.string.are_you_sure)
    }
}