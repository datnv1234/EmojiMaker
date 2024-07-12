package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.databinding.DialogSaveSuccessBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiViewModel
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class SaveSuccessDialog : BaseBindingDialogFragment<DialogSaveSuccessBinding>() {

    lateinit var home:() -> Unit
    lateinit var createMore: () -> Unit
    override val layoutId: Int
        get() = R.layout.dialog_save_success

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        val emojiViewModel: EmojiViewModel = ViewModelProvider(requireActivity())[EmojiViewModel::class.java]
        emojiViewModel.bitmapMutableLiveData.observe(this) {
            binding.imgPreview.setImageBitmap(it)
        }
        setUpLoadNativeAds()
    }

    private fun setUp() {
        binding.btnHome.setOnSafeClick {
            home.invoke()
            this@SaveSuccessDialog.dismiss()
        }
        binding.btnCreateMore.setOnSafeClick {
            createMore.invoke()
            this@SaveSuccessDialog.dismiss()
        }
    }


    private fun setUpLoadNativeAds(){
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MY_CREATIVE)
        ) {
            binding.rlNative.visible()
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_ADS_NATIVE_MY_CREATIVE)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            } else {
                loadNativeAds(getString(R.string.native_my_creative))
            }
        }else{
            binding.rlNative.gone()
        }
    }


    private fun loadNativeAds(keyAds: String) {
        if (!DeviceUtils.checkInternetConnection(requireContext()) || !FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MY_CREATIVE) ) {
            binding.rlNative.gone()
            return
        }

        view?.post {
            context?.let { context ->
                NativeAdsUtils.instance.loadNativeAds(
                    context,
                    keyAds
                ) { nativeAds ->
                    if (nativeAds != null && isAdded && isVisible) {
                        binding.frNativeAds.removeAllViews()
                        val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)

                        NativeAdsUtils.instance.populateNativeAdVideoView(
                            nativeAds,
                            adNativeVideoBinding.root as com.google.android.gms.ads.nativead.NativeAdView
                        )
                        binding.frNativeAds.addView(adNativeVideoBinding.root)
                        FirebaseAnalytics.getInstance(context)
                            .logEvent("d_load_native_ads_select_animal", null)

                    } else {
                        binding.rlNative.gone()
                        FirebaseAnalytics.getInstance(context)
                            .logEvent("e_load_native_ads_select_animal", null)

                    }
                }
            }
        }
    }
}