package com.wa.ai.emojimaker.ui.dialog

import android.content.DialogInterface
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
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class SharePackageDialog: BaseBindingDialogFragment<DialogShareBinding>() {

    private var isLoadNativeDone = false
    private var nativeConfig = ""
    var category: String? = null
    var addToWhatsapp: ((category: String) -> Unit)? = null
    var addToTelegram: ((category: String) -> Unit)? = null
    var download: ((category: String) -> Unit)? = null
    var share: ((category: String) -> Unit)? = null

    override val layoutId: Int
        get() = R.layout.dialog_share

    private val keyNative = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_MY_CREATIVE)
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

        setup()
    }

    private fun loadNativeUntilDone() {
        loadNativeAds()
        countDownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
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
            if (category != null)
                share?.invoke(category!!)
        }

        binding.btnDownload.setOnSafeClick {
            if (category != null)
                download?.invoke(category!!)
        }
    }

    private fun loadNativeAds() {
        if (!DeviceUtils.checkInternetConnection(requireContext())) binding.rlNative.gone()
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                requireContext(),
                keyNative
            ) { nativeAds ->

                if (nativeAds != null && isAdded && isVisible) {
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