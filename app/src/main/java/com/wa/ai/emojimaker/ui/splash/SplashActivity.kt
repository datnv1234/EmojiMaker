package com.wa.ai.emojimaker.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.wa.ai.emojimaker.App
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivitySplashBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.intro.IntroActivity
import com.wa.ai.emojimaker.ui.main.MainActivity
import com.wa.ai.emojimaker.ui.multilang.MultiLangActivity
import com.wa.ai.emojimaker.utils.extention.isGrantNotificationPermission
import com.wa.ai.emojimaker.utils.extention.setStatusBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {

    val bundle = Bundle()

    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadOpenAds()
    }



    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
        viewModel.fetchTokenRemoteConfig()
        binding.imgLaunch.postDelayed(
            {
                openMainActivity()
            }, 5000
        )
    }


    override fun setupData() {
        viewModel.typeNextScreen.observe(this) { type ->
            with(type) {
                when (this) {
                    Constant.TYPE_SHOW_LANGUAGE_ACT -> {
                        Intent(this@SplashActivity, MultiLangActivity::class.java).apply {
                            putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SPLASH)
                            startActivity(this)
                        }
                    }

                    Constant.TYPE_SHOW_INTRO_ACT -> {
                        startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                    }

                    Constant.TYPE_SHOW_PERMISSION -> {
                        val isGrantNotification = isGrantNotificationPermission()
                        val isNextScreen =
                            SharedPreferenceHelper.getBoolean(Constant.KEY_CLICK_GO, false)
                        if (isGrantNotification) {
                            lifecycleScope.launch(Dispatchers.IO) {

                                withContext(Dispatchers.Main) {
                                    if (isNextScreen) {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                MainActivity::class.java
                                                //PermissionActivity::class.java
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            if (isNextScreen) {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            } else {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            }
                        }
                    }

                    else -> {
                        startActivity(
                            Intent(this@SplashActivity, MainActivity::class.java)
                        )
                    }
                }
                finish()
            }
        }
    }

    private fun openMainActivity() {
        viewModel.getTypeNextScreen()
    }

    private fun loadOpenAds() {
        val countDownTimer: CountDownTimer = object : CountDownTimer(SPLASH_DELAY, COUNT_DOWN_INTERVAL) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                FirebaseAnalytics.getInstance(this@SplashActivity).logEvent("d_load_open_ads",bundle)
                val application = application
                (application as App).showAdIfAvailable(
                    this@SplashActivity,
                    object : App.OnShowAdCompleteListener {
                        override fun onAdShown() {
                            openMainActivity()
                        }
                    })
            }
        }
        countDownTimer.start()
    }

    companion object {
        private const val SPLASH_DELAY: Long = 10000
        private const val COUNT_DOWN_INTERVAL: Long = 1000
    }
}