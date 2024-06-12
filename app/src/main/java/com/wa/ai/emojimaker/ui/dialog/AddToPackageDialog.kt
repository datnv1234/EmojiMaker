package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.databinding.DialogAddToPackageBinding
import com.wa.ai.emojimaker.ui.adapter.PackageAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class AddToPackageDialog : BaseBindingDialogFragment<DialogAddToPackageBinding>() {

    private var isLoadNativeDone = false
    lateinit var save: ((pkg: PackageModel?) -> Unit)
    lateinit var createNewPackage: ((binding : DialogAddToPackageBinding) -> Unit)

    override val layoutId: Int
        get() = R.layout.dialog_add_to_package

    lateinit var viewModel: AddToPackageViewModel

    private val packageAdapter : PackageAdapter by lazy {
        PackageAdapter()
    }

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

        viewModel = ViewModelProvider(this)[AddToPackageViewModel::class.java]
        viewModel.getPackage(requireContext())
        viewModel.packageMutableLiveData.observe(this) {
            packageAdapter.submitList(it.toMutableList())
        }
        binding.rvPackage.adapter = packageAdapter
        setup()
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

    private fun getPackage() : PackageModel? = packageAdapter.getCurrentPackage()

    private fun setup() {
        binding.btnCreateNewPackage.setOnSafeClick {
            createNewPackage.invoke(binding)
            dismiss()
        }
        binding.btnSave.setOnSafeClick {
            if (getPackage() == null) {
                toast(getString(R.string.please_input_package_name))
                return@setOnSafeClick
            }
            dismiss()
            save.invoke(getPackage())
        }
        binding.bg.setOnSafeClick {
            dismiss()
        }
        binding.btnClose.setOnSafeClick {
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