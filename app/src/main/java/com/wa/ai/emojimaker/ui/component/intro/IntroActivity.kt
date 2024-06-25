package com.wa.ai.emojimaker.ui.component.intro

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.adjust.sdk.Adjust
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivityIntroBinding
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.ui.adapter.IntroAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.setStatusBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntroActivity : BaseBindingActivity<ActivityIntroBinding, IntroViewModel>() {

    private var isLoadNativeDone = false
    private val introAdapter: IntroAdapter by lazy { IntroAdapter() }
//    private val keyNative = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_NATIVE_INTRO)
    private val keyNative = "ca-app-pub-3940256099942544/2247696110"
    override val layoutId: Int
        get() = R.layout.activity_intro

    val countDownTimer: CountDownTimer = object : CountDownTimer(25000, 5000) {
        override fun onTick(millisUntilFinished: Long) {
            if (!isLoadNativeDone) {
                loadNativeAds()
            }
        }
        override fun onFinish() {
        }
    }

    override fun getViewModel(): Class<IntroViewModel> = IntroViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
        setUpViewPager()
        initListener()
    }

    override fun setupData() {

        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_INTRO)) {
            loadNativeUntilDone()
        } else {
            binding.rlNative.visibility = View.GONE
        }

        viewModel.getIntro(this)
        viewModel.introMutableLiveData.observe(this) {
            introAdapter.submitList(it.toMutableList())
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

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    private fun loadNativeUntilDone() {
        loadNativeAds()
        countDownTimer.start()
    }

    private fun startMainActivity() {
        SharedPreferenceHelper.storeBoolean("isFirstRun", false)
        Intent(this@IntroActivity, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun initListener() {
        binding.btnNext.setOnSafeClick(1200) {
            viewModel.introMutableLiveData.value?.size?.let { size ->
                with(binding.viewPager.currentItem) {
                    if (this < size - 1) {
                        binding.viewPager.currentItem = this + 1
                    } else if (this == size - 1) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            SharedPreferenceHelper.storeInt(
                                Constant.KEY_FIRST_SHOW_INTRO,
                                Constant.TYPE_SHOW_LANGUAGE_ACT
                            )

                            withContext(Dispatchers.Main) {
                                startMainActivity()
                                finish()
                            }
                        }

                    }
                }
            }

        }
    }

    private fun setUpViewPager() {
        binding.viewPager.apply {
            adapter = introAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == introAdapter.currentList.size - 1) {
                        binding.btnNext.text = getString(R.string.start)
                    } else {
                        binding.btnNext.text = getString(R.string.next)
                    }
                }
            })
            binding.dotsIndicator.attachTo(this)
        }
    }

    private fun loadNativeAds() {
        if (!DeviceUtils.checkInternetConnection(applicationContext)) binding.rlNative.visibility = View.GONE
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                applicationContext,
                keyNative
            ) { nativeAds ->
                if (nativeAds != null) {
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
