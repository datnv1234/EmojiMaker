package com.wa.ai.emojimaker.ui.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.AdNativeContentBinding
import com.wa.ai.emojimaker.databinding.DialogSaveBinding
import com.wa.ai.emojimaker.functions.Utils
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiViewModel
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class SaveStickerDialog(val activity: Activity) : BaseBindingDialogFragment<DialogSaveBinding>() {

    var addToPackage: (() -> Unit)? = null
    var download: ((binding : DialogSaveBinding) -> Unit)? = null
    var share: ((binding : DialogSaveBinding) -> Unit)? = null

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_CREATE_EMOJI)

    override val layoutId: Int
        get() = R.layout.dialog_save

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        val emojiViewModel : EmojiViewModel = ViewModelProvider(requireActivity())[EmojiViewModel::class.java]
        emojiViewModel.bitmapMutableLiveData.observe(this) {
            binding.imgPreview.setImageBitmap(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadNativeAd()
    }

    private fun setUp() {
        isCancelable = false
        binding.btnAddToPackage.setOnSafeClick {
            addToPackage?.invoke()
            dismiss()
        }
        binding.btnDownload.setOnSafeClick {
            download?.invoke(binding)
            Utils.saveImage(binding.imgPreview, requireContext())
            Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show()
            //dismiss()
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
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_CREATE_EMOJI)
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
                    if (nativeAds != null && isAdded) {
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

                }
            )
        }

    }
}