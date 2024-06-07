package com.wa.ai.emojimaker.ui.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.databinding.DialogCreatePackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class CreatePackageDialog : BaseBindingDialogFragment<DialogCreatePackageBinding>() {

    var bitmap: Bitmap? =  null
    var confirm: ((pkg : PackageModel) -> Unit)? = null
    private var isLoadNativeDone = false
    override val layoutId: Int
        get() = R.layout.dialog_create_package

    private val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        if (mFirebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MY_CREATIVE)) {
            val adConfig = mFirebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_NATIVE_MY_CREATIVE)
            if (adConfig.isNotEmpty()) {
                loadNativeUntilDone(adConfig)
            }
            else {
                loadNativeUntilDone(getString(R.string.native_my_creative))
            }
        } else {
            binding.rlNative.visibility = View.GONE
        }
        setUp()
    }

    private fun loadNativeUntilDone(adConfig: String) {
        val countDownTimer: CountDownTimer = object : CountDownTimer(25000, 5000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isLoadNativeDone) {
                    loadNativeAds(adConfig)
                }
            }
            override fun onFinish() {
            }
        }
        countDownTimer.start()
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

                //EventBus.getDefault().post(CreatePackageEvent(mPackage, path))

                confirm?.invoke(mPackage)
                dismiss()
            }
        }
        binding.btnCancel.setOnSafeClick {
            dismiss()
        }
    }

    private fun loadNativeAds(keyAds:String) {
        if (!DeviceUtils.checkInternetConnection(requireContext())) binding.rlNative.visibility = View.GONE
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                requireContext(),
                keyAds
            ) { nativeAds ->

                if (nativeAds != null && isAdded && isVisible) {
                    if (isDetached) {
                        nativeAds.destroy()
                        return@loadNativeAds
                    }
                    //binding.frNativeAds.removeAllViews()
                    val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                    NativeAdsUtils.instance.populateNativeAdVideoView(
                        nativeAds,
                        adNativeVideoBinding.root as NativeAdView
                    )
                    binding.frNativeAds.addView(adNativeVideoBinding.root)
                    isLoadNativeDone = true
                } else {
                    //binding.rlNative.visibility = View.GONE
                }
            }
        }
    }
}