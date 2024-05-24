package com.wa.ai.emojimaker.ui.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.DialogCreatePackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class CreatePackageDialog : BaseBindingDialogFragment<DialogCreatePackageBinding>() {

    lateinit var bitmap: Bitmap
    lateinit var confirm: ((pkg : PackageModel) -> Unit)
    private var isCreatePackage = false
    override val layoutId: Int
        get() = R.layout.dialog_create_package

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
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
                //Save
                isCreatePackage = true

                //EventBus.getDefault().post(CreatePackageEvent(mPackage, path))

                confirm.invoke(mPackage)
                dismiss()
            }
        }
        binding.btnCancel.setOnSafeClick {
            dismiss()
        }
    }
}