package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.databinding.DialogSaveBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiViewModel
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class SaveStickerDialog : BaseBindingDialogFragment<DialogSaveBinding>() {

    private var isLoadNativeDone = false

    var addToPackage: (() -> Unit)? = null
    var download: ((binding : DialogSaveBinding) -> Unit)? = null
    var share: ((binding : DialogSaveBinding) -> Unit)? = null

    override val layoutId: Int
        get() = R.layout.dialog_save

    private val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
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
        if (mFirebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_DIALOG)) {
            loadNativeUntilDone()
        } else {
            binding.rlNative.gone()
        }

        setUp()

        val emojiViewModel : EmojiViewModel = ViewModelProvider(requireActivity())[EmojiViewModel::class.java]
        emojiViewModel.bitmapMutableLiveData.observe(this) {
            binding.imgPreview.setImageBitmap(it)
        }
        /*if (bitmap != null) {
            binding.imgPreview.setImageBitmap(bitmap)
        }*/
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    private fun loadNativeUntilDone() {
        loadNativeAds()
        countDownTimer.start()
    }

    private fun setUp() {
        isCancelable = false
        binding.btnAddToPackage.setOnSafeClick {
            addToPackage?.invoke()
            dismiss()
        }
        binding.btnDownload.setOnSafeClick {
            download?.invoke(binding)
            dismiss()
        }
        binding.btnShare.setOnSafeClick {
            share?.invoke(binding)
            dismiss()
        }
        binding.btnClose.setOnSafeClick {
            dismiss()
        }
        binding.bg.setOnSafeClick {
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
                    binding.rlNative.visibility = View.GONE
                }
            }
        }
    }
}