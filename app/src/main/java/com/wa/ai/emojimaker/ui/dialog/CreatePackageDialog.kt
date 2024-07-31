package com.wa.ai.emojimaker.ui.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.nativead.NativeAdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.DialogCreatePackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiViewModel
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class CreatePackageDialog : BaseBindingDialogFragment<DialogCreatePackageBinding>() {

    var bitmap: Bitmap? =  null
    var confirm: ((pkg : PackageModel) -> Unit)? = null
    private var adView: NativeAdView? = null

    override val layoutId: Int
        get() = R.layout.dialog_create_package

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        val emojiViewModel : EmojiViewModel = ViewModelProvider(requireActivity())[EmojiViewModel::class.java]
        emojiViewModel.nativeAdAddToPackageDialog.observe(this) {
            adView = it
            addNativeAd()
        }
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
        binding.btnCancel.setOnSafeClick {
            dismiss()
        }
    }

    private fun addNativeAd() {
        adView?.let {
            val adContainer = binding.frNativeAds
            if (it.parent != null) {
                (it.parent as ViewGroup).removeView(adView)
            }
            adContainer.removeAllViews()
            adContainer.addView(it)
        }
    }
}