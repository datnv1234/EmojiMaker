package com.wa.ai.emojimaker.ui.dialog

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.adjust.sdk.Adjust
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.AdNativeContentBinding
import com.wa.ai.emojimaker.databinding.DialogLoadingBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class DialogLoading(val activity: Activity) : BaseBindingDialogFragment<DialogLoadingBinding>() {

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_MERGE_EMOJI)

    var onClickDone: (() -> Unit) = {}

    override val layoutId: Int
        get() = R.layout.dialog_loading

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initAction()
        val countDownTimer = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                done()
            }
        }
        countDownTimer.start()
        loadNativeAd()
    }

    private fun initAction() {
        binding.btnDone.setOnSafeClick {
            dismiss()
            onClickDone.invoke()
        }
    }

    private fun done() {
        binding.animLoading.gone()
        binding.btnDone.visible()
        binding.tvMerging.text = getString(R.string.merge_successfully)
    }

    private fun loadNativeAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_MERGE_EMOJI)
        ) {
            loadNativeAds(keyNative)
        } else {
            binding.rlNative.visibility = View.GONE
        }
    }

    private fun loadNativeAds(keyAds: String) {
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                activity,
                keyAds,
                { nativeAds ->
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
                },
                {
                    dismiss()
                    onClickDone.invoke()
                }
            )
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
}