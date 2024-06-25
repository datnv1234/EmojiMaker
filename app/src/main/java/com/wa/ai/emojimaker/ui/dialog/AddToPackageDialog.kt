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
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class AddToPackageDialog : BaseBindingDialogFragment<DialogAddToPackageBinding>() {

    private var isLoadNativeDone = false
    lateinit var save: ((pkg: PackageModel?) -> Unit)
    lateinit var createNewPackage: ((binding : DialogAddToPackageBinding) -> Unit)

    private val keyNative = "ca-app-pub-3940256099942544/2247696110"

    override val layoutId: Int
        get() = R.layout.dialog_add_to_package

    lateinit var viewModel: AddToPackageViewModel

    private val packageAdapter : PackageAdapter by lazy {
        PackageAdapter().apply {
            callBack = { _, _ ->
                binding.btnSave.isEnabled = true
            }
        }
    }



    val countDownTimer: CountDownTimer = object : CountDownTimer(25000, 5000) {
        override fun onTick(millisUntilFinished: Long) {
            if (!isLoadNativeDone) {
                loadNativeAds()
            }
        }
        override fun onFinish() {
        }
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_DIALOG)) {
            loadNativeUntilDone()
        } else {
            binding.rlNative.gone()
        }

        viewModel = ViewModelProvider(this)[AddToPackageViewModel::class.java]
        viewModel.getPackage(requireContext())
        viewModel.packageMutableLiveData.observe(this) {
            packageAdapter.submitList(it.toMutableList())
        }
        binding.rvPackage.adapter = packageAdapter
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
    private fun loadNativeUntilDone() {
        loadNativeAds()
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

    private fun loadNativeAds() {
        if (!DeviceUtils.checkInternetConnection(requireContext())) binding.rlNative.visibility = View.GONE
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                requireContext(),
                keyNative
            ) { nativeAds ->

                if (nativeAds != null && isAdded && isVisible) {
                    //binding.frNativeAds.removeAllViews()
                    val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                    NativeAdsUtils.instance.populateNativeAdVideoView(
                        nativeAds,
                        adNativeVideoBinding.root as NativeAdView
                    )
                    binding.frNativeAds.addView(adNativeVideoBinding.root)
                    isLoadNativeDone = true
                } else {
                    binding.rlNative.visibility = View.GONE
                }
            }
        }
    }
}