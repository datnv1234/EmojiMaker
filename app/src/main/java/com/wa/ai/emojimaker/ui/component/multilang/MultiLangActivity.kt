package com.wa.ai.emojimaker.ui.component.multilang

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.adjust.sdk.Adjust
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivityMultiLangBinding
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.ui.adapter.MultiLangAdapter
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.component.intro.IntroActivity
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.SystemUtil
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.invisible
import com.wa.ai.emojimaker.utils.extention.visible

class MultiLangActivity : BaseBindingActivity<ActivityMultiLangBinding, MultiLangViewModel>() {

	private var isLoadNativeDone = false
	private var nativeConfig = ""


	private var type: Int = 0
	private var currentPosLanguage = 0
	private var oldCode = "en"
	private var code = ""

	private val multiLangAdapter: MultiLangAdapter by lazy {
		MultiLangAdapter().apply {
			callBack = { pos, item ->
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
		val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
		nativeConfig = firebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_NATIVE_LANGUAGE)
		if (firebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_LANGUAGE)) {
			loadNativeUntilDone()
		}

		viewModel.getListLanguage()
		viewModel.languageLiveData.observe(this) { it ->
			multiLangAdapter.submitList(it)
			it.indexOfFirst { it.code == code }.let { pos ->
				currentPosLanguage = pos
				multiLangAdapter.newPosition = pos
			}
		}
	}

	val countDownTimer: CountDownTimer = object : CountDownTimer(25000, 5000) {
		override fun onTick(millisUntilFinished: Long) {
			if (!isLoadNativeDone) {
				loadNativeAds(nativeConfig)
			}
		}
		override fun onFinish() {
		}
	}
	private fun loadNativeUntilDone() {
		countDownTimer.start()
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

	private fun updateUIForType(type: Int) {

		when (type) {
			Constant.TYPE_LANGUAGE_SPLASH -> {
				val isFirstRun = SharedPreferenceHelper.getBoolean("isFirstRun", true)
				binding.imgBack.invisible()
				binding.imgChooseLang.visible()
				binding.imgChooseLang.setOnClickListener {
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
				binding.imgChooseLang.invisible()
				binding.imgBack.setOnClickListener {
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
					isLoadNativeDone = true
				} else {
//					Log.d(TAG, "loadNativeAds: failed")
					//binding.rlNative.visibility = View.GONE
				}
			}
		}

	}

}

