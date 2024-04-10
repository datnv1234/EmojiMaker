package com.wa.ai.emojimaker.ui.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivityIntroBinding
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.ui.adapter.IntroAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.BannerUtils
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.setStatusBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntroActivity : BaseBindingActivity<ActivityIntroBinding, IntroViewModel>() {

    private val introAdapter: IntroAdapter by lazy { IntroAdapter() }
    private lateinit var mFirebaseRemoteConfig : FirebaseRemoteConfig
    override val layoutId: Int
        get() = R.layout.activity_intro

    override fun getViewModel(): Class<IntroViewModel> = IntroViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
        setUpViewPager()
        initListener()
    }

    override fun setupData() {
        viewModel.getIntro(this)
        viewModel.introMutableLiveData.observe(this) {
            introAdapter.submitList(it.toMutableList())
        }
    }

    override fun onStart() {
        super.onStart()
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        if (mFirebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_INTRO)) {
            val adConfig = mFirebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_NATIVE_INTRO)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            }
            else {
                loadNativeAds(getString(R.string.native_intro))
            }
        } else {
            binding.rlNative.visibility = View.GONE
        }

    }
    private fun initListener() {
        binding.tvNext.setOnSafeClick(1200) {
            viewModel.introMutableLiveData.value?.size?.let { size ->
                with(binding.viewPager.currentItem) {
                    if (this < size - 1) {
                        binding.viewPager.currentItem = this + 1
                    } else if (this == size - 1) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            SharedPreferenceHelper.storeInt(
                                Constant.KEY_FIRST_SHOW_INTRO,
                                Constant.TYPE_SHOW_PERMISSION
                            )

                            withContext(Dispatchers.Main) {
//                                Intent(this@IntroActivity, PermissionActivity::class.java).apply {
//                                    startActivity(this)
//                                }
                                //finish()
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
                        binding.tvNext.text = getString(R.string.start)
                    } else {
                        binding.tvNext.text = getString(R.string.next)
                    }
                }
            })
            binding.dotsIndicator.attachTo(this)
        }
    }

    private fun loadNativeAds(keyAds:String) {
        if (!DeviceUtils.checkInternetConnection(applicationContext)) binding.rlNative.visibility = View.GONE
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                applicationContext,
                keyAds
            ) { nativeAds ->
                if (nativeAds != null) {
                    //binding.frNativeAds.removeAllViews()
                    val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                    NativeAdsUtils.instance.populateNativeAdVideoView(
                        nativeAds,
                        adNativeVideoBinding.root as NativeAdView
                    )
                    binding.frNativeAds.addView(adNativeVideoBinding.root)
                } else {
                    binding.rlNative.visibility = View.GONE
                }
            }
        }

    }

    private fun loadBanner(keyAdsBanner:String) {
        var timeDelay : Long = 2000
        if (!DeviceUtils.checkInternetConnection(this)) binding.rlBanner.visibility = View.GONE
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_INTRO)) {
            val adConfig = FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.KEY_COLLAPSE_RELOAD_TIME)
            if (adConfig != 0L) {
                timeDelay = adConfig * 1000
            }
        }
        BannerUtils.instance?.loadCollapsibleBanner(this, keyAdsBanner, timeDelay)
    }
}
