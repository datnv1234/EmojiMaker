package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.DialogShareBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class SharePackageDialog: BaseBindingDialogFragment<DialogShareBinding>() {

    var category: String? = null
    var addToWhatsapp: ((category: String) -> Unit)? = null
    var addToTelegram: ((category: String) -> Unit)? = null
    var download: ((category: String) -> Unit)? = null
    var share: ((binding : DialogShareBinding) -> Unit)? = null

    override val layoutId: Int
        get() = R.layout.dialog_share

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setup()
    }

    private fun setup() {
        binding.bg.setOnSafeClick {
            dismiss()
        }

        binding.btnAddToWhatsapp.setOnSafeClick {
            if (category != null) {
                addToWhatsapp?.invoke(category!!)
            }
        }

        binding.btnAddToTelegram.setOnSafeClick {
            if (category != null) {
                addToTelegram?.invoke(category!!)
            }
        }

        binding.btnShare.setOnSafeClick {
            share?.invoke(binding)
        }

        binding.btnDownload.setOnSafeClick {
            download?.invoke(category!!)
        }
    }
}