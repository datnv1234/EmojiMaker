package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogShareBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class SharePackageDialog: BaseBindingDialogFragment<DialogShareBinding>() {

    var addToWhatsapp: ((binding : DialogShareBinding) -> Unit)? = null
    var addToTelegram: ((binding : DialogShareBinding) -> Unit)? = null
    var download: ((binding : DialogShareBinding) -> Unit)? = null
    var share: ((binding : DialogShareBinding) -> Unit)? = null

    override val layoutId: Int
        get() = R.layout.dialog_share

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setup()
    }

    private fun setup() {
        binding.mainView.setOnSafeClick {
            dismiss()
        }

        binding.btnAddToWhatsapp.setOnSafeClick {
            addToWhatsapp?.invoke(binding)
        }

        binding.btnAddToTelegram.setOnSafeClick {
            addToTelegram?.invoke(binding)
        }

        binding.btnShare.setOnSafeClick {
            share?.invoke(binding)
        }

        binding.btnDownload.setOnSafeClick {
            download?.invoke(binding)
        }
    }
}