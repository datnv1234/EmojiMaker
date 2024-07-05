package com.wa.ai.emojimaker.ui.component.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import com.wa.ai.emojimaker.App
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant.ADS
import com.wa.ai.emojimaker.databinding.ActivityMainBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.BannerUtils
import com.wa.ai.emojimaker.utils.extention.gone
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    private var keyAdInter: String =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_INTER_HOME_SCREEN)
    private val interDelay = FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.INTER_DELAY)
    private val keyAdsBanner =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_BANNER_MAIN)
    private val bannerReload =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)

    var mInterstitialAd: InterstitialAd? = null
    private var analytics: FirebaseAnalytics? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null

    private val REQUEST_CODE_UPDATE = 1001
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdateListener)
        }
        checkForAppUpdates()
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setUpDialogPermission()
        val toolbar: Toolbar = binding.toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController((navController))
    }

    override fun setupData() {
        loadAds()
        viewModel.getSuggestStickers(this)
        viewModel.getCategories(this)
        viewModel.getPackage(this)
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()

        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
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

    private fun loadAds() {
        setUpLoadInterAds()
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_MAIN)
        ) {
            loadBanner()
        } else {
            binding.rlBanner.gone()
        }
        viewModel.loadBanner.observe(this) {
            loadBanner()
        }
    }

    private fun loadBanner() {
        viewModel.starTimeCountReloadBanner(bannerReload)
        BannerUtils.instance?.loadCollapsibleBanner(this, keyAdsBanner) {

        }
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
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdateListener)
        }
    }

    private val installStateUpdateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
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

    fun showInterstitial() {
        val isShowAd = (Date().time - App.adTimeStamp) > interDelay
        if (isShowAd) {
            mInterstitialAd?.fullScreenContentCallback =
                object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        App.adTimeStamp = Date().time
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    }
                }
            mInterstitialAd?.show(this@MainActivity)
            loadInterAds()
        }
    }

    var loadInterCount = 0
    fun loadInterAds() {
        InterstitialAd.load(
            this,
            keyAdInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    Timber.tag(ADS).d("onAdFailedToShowFullScreenContent: ${loadAdError.message}")
                    Timber.tag(ADS).d("onAdFailedToShowFullScreenContent: ${loadAdError.cause}")
                    if (loadInterCount < 3) {
                        loadInterAds()
                        loadInterCount++
                    }
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    loadInterCount = 0
                    mFirebaseAnalytics?.logEvent("d_load_inter", null)

                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue ->
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                interstitialAd.responseInfo.loadedAdapterResponseInfo
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
            })
    }

    private fun setUpLoadInterAds() {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_HOME_SCREEN)) {
            loadInterAds()
        }
    }

    companion object {
        const val ITEMS_PER_AD = 5
    }
}