package com.wa.ai.emojimaker.ui.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.bumptech.glide.Glide
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.databinding.AdNativeContentBinding
import com.wa.ai.emojimaker.databinding.DialogSaveSuccessBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiViewModel
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class SaveSuccessDialog(val activity: Activity) : BaseBindingDialogFragment<DialogSaveSuccessBinding>() {

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_HOME)

    lateinit var home:() -> Unit
    lateinit var createMore: () -> Unit
    var emojiUrl : String? = null
    override val layoutId: Int
        get() = R.layout.dialog_save_success

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUp()
        val emojiViewModel: EmojiViewModel = ViewModelProvider(requireActivity())[EmojiViewModel::class.java]
        emojiViewModel.bitmapMutableLiveData.observe(this) {
            binding.imgPreview.setImageBitmap(it)
        }
        emojiUrl?.let {
            Glide.with(this@SaveSuccessDialog)
                .load(it)
                .into(binding.imgPreview)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadNativeAd()
    }

    private fun setUp() {
        binding.btnHome.setOnSafeClick {
            this@SaveSuccessDialog.dismiss()
            home.invoke()
        }
        binding.btnCreateMore.setOnSafeClick {
            this@SaveSuccessDialog.dismiss()
            createMore.invoke()
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