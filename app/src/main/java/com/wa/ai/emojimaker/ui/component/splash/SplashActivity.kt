package com.wa.ai.emojimaker.ui.component.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivitySplashBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.ui.component.intro.IntroActivity
import com.wa.ai.emojimaker.ui.component.main.MainActivity
import com.wa.ai.emojimaker.ui.component.multilang.MultiLangActivity
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.extention.isGrantNotificationPermission
import com.wa.ai.emojimaker.utils.extention.setStatusBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {

    private val REQUEST_CODE_UPDATE = 1000
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    val bundle = Bundle()
    private var mInterstitialAd: InterstitialAd? = null
    private var analytics: FirebaseAnalytics? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE){
            appUpdateManager.registerListener(installStateUpdateListener)
        }
        checkForAppUpdates()

    }
    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
        binding.imgLaunch.postDelayed(
            {
                openNextScreen()
            }, 5000
        )
    }

    private fun openNextScreen() {
        if (mInterstitialAd != null) {
            // Nếu quảng cáo đã tải xong, hiển thị quảng cáo và chuyển đến Activity mới sau khi quảng cáo kết thúc
            mInterstitialAd?.fullScreenContentCallback =
                object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        openMainActivity()
                        finish()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        openMainActivity()
                        finish()
                    }
                }
            mInterstitialAd?.show(this@SplashActivity)

            mFirebaseAnalytics?.logEvent("v_inter_ads_splash", null)
        }else{
            openMainActivity()
            finish()
        }

    }

    override fun setupData() {
        setUpLoadInterAds()
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

    override fun onResume() {
        super.onResume()
        Adjust.onResume()

        if (updateType == AppUpdateType.IMMEDIATE){
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        AppUpdateType.FLEXIBLE,
                        this,
                        REQUEST_CODE_UPDATE
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    REQUEST_CODE_UPDATE
                )
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE){
            if (resultCode != RESULT_OK){
                Toast.makeText(this,"Something went wrong !", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE){
            appUpdateManager.unregisterListener(installStateUpdateListener)
        }
    }

    private val installStateUpdateListener = InstallStateUpdatedListener{state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED){
            Toast.makeText(
                applicationContext,
                "Download successful. Restarting app in 5 seconds.",
                Toast.LENGTH_LONG
            ).show()
            lifecycleScope.launch {
                delay(5000)
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun openMainActivity() {
        viewModel.getTypeNextScreen()
    }

    private fun loadInterAdsSplash(keyAdsInter: String) {

        InterstitialAd.load(
            this@SplashActivity,
            keyAdsInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mFirebaseAnalytics?.logEvent("d_load_inter_splash", null)

                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue -> // Lấy thông tin về nhà cung cấp quảng cáo
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                interstitialAd.responseInfo.loadedAdapterResponseInfo
                            // Gửi thông tin doanh thu quảng cáo đến Adjust
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros.toDouble() / 1000000.0
                            adRevenue.setRevenue(
                                revenue,
                                adValue.currencyCode
                            )
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)
                            analytics = FirebaseAnalytics.getInstance(applicationContext)
                            val params = Bundle()
                            params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "admob mediation")
                            params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                            params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                            params.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                            params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            analytics?.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    mFirebaseAnalytics?.logEvent("e_load_inter_splash", null)

                }
            })

    }

    private fun setUpLoadInterAds() {
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        if (firebaseRemoteConfig.getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_SPLASH)) {
            val adConfig = firebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_INTER_SPLASH)
            if (adConfig.isNotEmpty()) {
                loadInterAdsSplash(adConfig)
            } else {
                loadInterAdsSplash(getString(R.string.inter_splash))
            }
        }
    }

    companion object {
        private const val SPLASH_DELAY: Long = 10000
        private const val COUNT_DOWN_INTERVAL: Long = 1000
    }
}