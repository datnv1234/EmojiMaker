package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.AdNativeContentBinding
import com.wa.ai.emojimaker.databinding.DialogAddToPackageBinding
import com.wa.ai.emojimaker.ui.adapter.PackageAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class AddToPackageDialog : BaseBindingDialogFragment<DialogAddToPackageBinding>() {

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)

    lateinit var save: ((pkg: PackageModel?) -> Unit)
    lateinit var createNewPackage: ((binding: DialogAddToPackageBinding) -> Unit)

    override val layoutId: Int
        get() = R.layout.dialog_add_to_package

    lateinit var viewModel: AddToPackageViewModel

    private val packageAdapter: PackageAdapter by lazy {
        PackageAdapter().apply {
            callBack = { _, _ ->

            }
        }
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AddToPackageViewModel::class.java]
        viewModel.getPackage(requireContext())
        viewModel.packageMutableLiveData.observe(this) {
            packageAdapter.submitList(it.toMutableList())
            if (it.isNotEmpty()) {
                binding.btnSave.isEnabled = true
            }
        }
        binding.rvPackage.adapter = packageAdapter
        setup()
        loadNativeAd()
    }

    private fun getPackage(): PackageModel? = packageAdapter.getCurrentPackage()

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