package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.databinding.DialogShareBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class SharePackageDialog: BaseBindingDialogFragment<DialogShareBinding>() {

    private var isLoadNativeDone = false

    var category: String? = null
    var addToWhatsapp: ((category: String) -> Unit)? = null
    var addToTelegram: ((category: String) -> Unit)? = null
    var download: ((category: String) -> Unit)? = null
    var share: ((category: String) -> Unit)? = null

    override val layoutId: Int
        get() = R.layout.dialog_share

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

    private fun setup() {
        binding.bg.setOnSafeClick {
            dismiss()
        }

        binding.btnAddToWhatsapp.setOnSafeClick {
            if (category != null) {
                addToWhatsapp?.invoke(category!!)
            }
        }

        binding.btnAddToTelegram.setOnSafeClick {
            if (category != null) {
                addToTelegram?.invoke(category!!)
            }
        }

        binding.btnShare.setOnSafeClick {
            share?.invoke(category!!)
        }

        binding.btnDownload.setOnSafeClick {
            download?.invoke(category!!)
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