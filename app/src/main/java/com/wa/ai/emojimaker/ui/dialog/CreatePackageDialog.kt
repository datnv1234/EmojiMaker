package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.adjust.sdk.Adjust
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.AdNativeContentBinding
import com.wa.ai.emojimaker.databinding.DialogCreatePackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible
import java.io.File

class CreatePackageDialog : BaseBindingDialogFragment<DialogCreatePackageBinding>() {

    var confirm: ((pkg : PackageModel) -> Unit)? = null
    var oldName: String = ""
    var createNew: Boolean = true

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)

    override val layoutId: Int
        get() = R.layout.dialog_create_package

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        loadNativeAd()
    }

    private fun setUp() {
        if (oldName.isNotEmpty())
            binding.edtPkgName.setText(oldName)
        binding.btnConfirm.setOnSafeClick {
            val text = binding.edtPkgName.text.toString()
            if (text == "") {
                toast(getString(R.string.please_input_package_name))
                return@setOnSafeClick
            } else if (text.isEmpty()) {
                toast(getString(R.string.please_input_package_name))
                return@setOnSafeClick
            }
            val mPackage = PackageModel(binding.edtPkgName.text.toString())
            val path : File? = if (!createNew) {
                DeviceUtils.renameInternalDir(requireContext(), Constant.INTERNAL_MY_CREATIVE_DIR, mPackage.id, PackageModel.getID(oldName))
            } else {
                DeviceUtils.mkInternalDir(requireContext(), Constant.INTERNAL_MY_CREATIVE_DIR, mPackage.id)
            }

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

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    private fun loadNativeAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_HOME)
        ) {
            loadNativeAds(keyNative)
        } else {
            binding.rlNative.visibility = View.GONE
        }
    }

    private fun loadNativeAds(keyAds: String) {
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                requireContext(),
                keyAds
            ) { nativeAds ->
                if (nativeAds != null && isAdded && isVisible) {
                    binding.rlNative.visible()
                    val adNativeVideoBinding = AdNativeContentBinding.inflate(layoutInflater)
                    NativeAdsUtils.instance.populateNativeAdVideoView(
                        nativeAds,
                        adNativeVideoBinding.root
                    )
                    binding.frNativeAds.removeAllViews()
                    binding.frNativeAds.addView(adNativeVideoBinding.root)
                }
            }
        }

    }
}