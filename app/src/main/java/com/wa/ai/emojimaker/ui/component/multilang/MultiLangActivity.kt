package com.wa.ai.emojimaker.ui.component.multilang

import android.content.Intent
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.databinding.ActivityMultiLangBinding
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.ui.adapter.MultiLangAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.component.intro.IntroActivity
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.SystemUtil
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.invisible
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class MultiLangActivity : BaseBindingActivity<ActivityMultiLangBinding, MultiLangViewModel>() {

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_LANGUAGE)

    private var type: Int = 0
    private var currentPosLanguage = 0
    private var oldCode = "en"
    private var code = ""

    private val multiLangAdapter: MultiLangAdapter by lazy {
        MultiLangAdapter().apply {
            callBack = { pos, item ->
                binding.btnChooseLang.visible()
                code = item.code ?: code
                currentPosLanguage = pos
            }
            binding.rcvLangs.adapter = this
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_multi_lang

    override fun getViewModel(): Class<MultiLangViewModel> = MultiLangViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {

        type = intent.getIntExtra(Constant.TYPE_LANG, 0)
        oldCode = SystemUtil.getPreLanguage(this)
        code = oldCode
        updateUIForType(type)
    }

    override fun setupData() {
        loadNativeAd()
        viewModel.getListLanguage()
        viewModel.languageLiveData.observe(this) {
            multiLangAdapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }



    private fun updateUIForType(type: Int) {

        when (type) {
            Constant.TYPE_LANGUAGE_SPLASH -> {
                //val isFirstRun = SharedPreferenceHelper.getBoolean("isFirstRun", true)
                val isFirstRun = true
                binding.imgBack.invisible()
                binding.btnChooseLang.setOnClickListener {
                    SystemUtil.changeLang(code.ifEmpty { oldCode }, this)
                    if (isFirstRun) {
                        gotoIntroActivity()
                    } else {
                        gotoMainActivity()
                    }
                    finish()
                }
            }

            Constant.TYPE_LANGUAGE_SETTING -> {
                binding.imgBack.visible()
                binding.imgBack.setOnSafeClick {
                    finish()
                }
                binding.btnChooseLang.setOnSafeClick {
                    if (oldCode != code) {
                        SystemUtil.changeLang(code.ifEmpty { oldCode }, this)
                        val i = Intent(this, MainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                    }
                    finish()
                }
            }
        }
    }

    private fun gotoIntroActivity() {
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun loadNativeAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_LANGUAGE)
        ) {
            loadNativeAds(keyNative)
        } else {
            binding.rlNative.gone()
        }
    }
    private fun loadNativeAds(keyAds: String) {
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                this,
                keyAds,
                { nativeAds ->
                    if (nativeAds != null) {
                        val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                        NativeAdsUtils.instance.populateNativeAdVideoView(
                            nativeAds,
                            adNativeVideoBinding.root as NativeAdView
                        )
                        binding.frNativeAds.removeAllViews()
                        binding.frNativeAds.addView(adNativeVideoBinding.root)
                    }
                },
                {
                    gotoIntroActivity()
                }
            )
        }
    }
}

