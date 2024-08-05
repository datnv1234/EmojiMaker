package com.wa.ai.emojimaker.ui.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.DialogCreatePackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.splash.SplashActivity
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class CreatePackageDialog : BaseBindingDialogFragment<DialogCreatePackageBinding>() {

    var bitmap: Bitmap? =  null
    var confirm: ((pkg : PackageModel) -> Unit)? = null

    override val layoutId: Int
        get() = R.layout.dialog_create_package

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        addNativeAd()
    }

    private fun setUp() {
        binding.btnConfirm.setOnSafeClick {
            val text = binding.edtPkgName.text.toString()
            if (text == "") {
                toast(getString(R.string.please_input_package_name))
                return@setOnSafeClick
            }
            val mPackage = PackageModel(binding.edtPkgName.text.toString())
            val path = DeviceUtils.mkInternalDir(requireContext(), Constant.INTERNAL_MY_CREATIVE_DIR, mPackage.id)
            if ( path == null) {
                toast(getString(R.string.package_existed))
            } else {
                confirm?.invoke(mPackage)
                dismiss()
            }
        }

        binding.btnClearText.setOnSafeClick {
            val emptyText = ""
            binding.edtPkgName.setText(emptyText)
        }
        binding.btnCancel.setOnSafeClick {
            dismiss()
        }

    }

    private fun addNativeAd() {
        SplashActivity.adNativeDialog?.let {
            binding.rlNative.visible()
            if (it.parent != null) {
                (it.parent as ViewGroup).removeView(it)
            }
            binding.frNativeAds.removeAllViews()
            binding.frNativeAds.addView(it)
        }
    }
}