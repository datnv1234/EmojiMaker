package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.nativead.NativeAdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.DialogAddToPackageBinding
import com.wa.ai.emojimaker.ui.adapter.PackageAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiViewModel
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class AddToPackageDialog : BaseBindingDialogFragment<DialogAddToPackageBinding>() {

    lateinit var save: ((pkg: PackageModel?) -> Unit)
    lateinit var createNewPackage: ((binding : DialogAddToPackageBinding) -> Unit)
    private var adView: NativeAdView? = null

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
    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AddToPackageViewModel::class.java]
        viewModel.getPackage(requireContext())
        viewModel.packageMutableLiveData.observe(this) {
            packageAdapter.submitList(it.toMutableList())
        }
        binding.rvPackage.adapter = packageAdapter
        setup()
        val emojiViewModel : EmojiViewModel = ViewModelProvider(requireActivity())[EmojiViewModel::class.java]
        emojiViewModel.nativeAdAddToPackageDialog.observe(this) {
            adView = it
            addNativeAd()
        }
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

    private fun addNativeAd() {
        binding.rlNative.visible()
        adView?.let {
            val adContainer = binding.frNativeAds
            if (it.parent != null) {
                (it.parent as ViewGroup).removeView(it)
            }
            adContainer.removeAllViews()
            adContainer.addView(it)
        }
    }
}