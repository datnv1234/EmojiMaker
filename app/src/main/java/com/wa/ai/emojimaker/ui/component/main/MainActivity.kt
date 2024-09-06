package com.wa.ai.emojimaker.ui.component.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
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
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.ActivityMainBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.AdsConsentManager
import com.wa.ai.emojimaker.utils.ads.BannerUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setFullScreen
import com.wa.ai.emojimaker.utils.notification.NotificationWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    private var adsConsentManager: AdsConsentManager? = null
    private val isAdsInitializeCalled = AtomicBoolean(false)
    private val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    private var mInterstitialAd: InterstitialAd? = null

    private var retryAttempt = 0.0

    private val keyAdInterAllPrice =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_INTER_HOME_SCREEN)

    private val keyAdBannerHigh = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_BANNER_MAIN_HIGH)
    private val keyAdBannerAllPrice = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.KEY_ADS_BANNER_MAIN)

    private val bannerReload =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)

    private val REQUEST_CODE_UPDATE = 1001
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    private val mContext = this
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
        initNotificationWorker()
        setUpDialogPermission()
        setFullScreen()
        val toolbar: Toolbar = binding.toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController((navController))
        initAdsManager()
    }

    override fun setupData() {
        loadAds()
        viewModel.getSuggestStickers(mContext)
        viewModel.getCategories(mContext)
        viewModel.getPackage(mContext)
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
                        mContext,
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
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_MAIN)
        ) {
            loadBanner()
        } else {
            binding.rlBanner.gone()
        }
        viewModel.loadBanner.observe(mContext) {
            loadBanner()
        }
    }

    private fun loadBanner() {
        viewModel.starTimeCountReloadBanner(bannerReload)
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_USE_BANNER_MONET)) {
            BannerUtils.instance?.loadCollapsibleBanner(this, keyAdBannerHigh) { res2 ->
                if (!res2) {
                    BannerUtils.instance?.loadCollapsibleBanner(this, keyAdBannerAllPrice) { }
                }
            }
        } else {
            BannerUtils.instance?.loadCollapsibleBanner(this, keyAdBannerAllPrice) { }
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
                    mContext,
                    REQUEST_CODE_UPDATE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(mContext, "Something went wrong !", Toast.LENGTH_SHORT).show()
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

    private fun initAdsManager() {
        adsConsentManager = AdsConsentManager.getInstance(mContext)
        adsConsentManager?.gatherConsent(mContext) { consentError ->
            if (consentError != null) {

                initializeMobileAdsSdk()
            }

            if (adsConsentManager?.canRequestAds == true) {
                initializeMobileAdsSdk()
            }
        }

        if (adsConsentManager?.canRequestAds == true) {
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isAdsInitializeCalled.getAndSet(true)) {
            return
        }
        try {
            MobileAds.initialize(this) {}
        } catch (e: Exception) {
            Timber.e(e)
        }

        loadInterAd()
    }

    private fun initNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val myRequest =
            PeriodicWorkRequest.Builder(NotificationWorker::class.java, 12, TimeUnit.HOURS)
                .setConstraints(constraints).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("my_id", ExistingPeriodicWorkPolicy.KEEP, myRequest)
    }

    private fun loadInterAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_HOME_SCREEN)
        ) {
            loadInterAdsMain(keyAdInterAllPrice)
        }
    }

    private fun loadInterAdsMain(keyAdInter: String) {
        InterstitialAd.load(
            this,
            keyAdInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mFirebaseAnalytics.logEvent("e_load_inter_splash", null)
                    mInterstitialAd = null

                    Handler(Looper.getMainLooper()).postDelayed({ loadInterAdsMain(keyAdInter) }, 2000)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    mFirebaseAnalytics.logEvent("d_load_inter_splash", null)
                    mInterstitialAd = ad
                    retryAttempt = 0.0
                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue ->
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                mInterstitialAd?.responseInfo?.loadedAdapterResponseInfo
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros.toDouble() / 1000000.0
                            adRevenue.setRevenue(revenue, adValue.currencyCode)
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)

                            val analytics = FirebaseAnalytics.getInstance(this@MainActivity)
                            val params = Bundle().apply {
                                putString(
                                    FirebaseAnalytics.Param.AD_PLATFORM,
                                    "admob mediation"
                                )
                                putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                                putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                                putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                                putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            }
                            analytics.logEvent("ad_impression_2", params)
                        }
                }
            }
        )
    }

    private fun loadInterAdsSplashSequence(listKeyAds: List<String>) {

        fun loadInterAds(adIndex: Int) {
            if (adIndex == listKeyAds.size - 1) {
                loadInterAdsMain(listKeyAds.last())
                return
            }
            InterstitialAd.load(
                this,
                listKeyAds[adIndex],
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mFirebaseAnalytics.logEvent("e_load_inter_splash", null)
                        mInterstitialAd = null
                        loadInterAds(adIndex + 1)
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        mFirebaseAnalytics.logEvent("d_load_inter_splash", null)
                        mInterstitialAd = ad
                        mInterstitialAd?.onPaidEventListener =
                            OnPaidEventListener { adValue ->
                                val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                    mInterstitialAd?.responseInfo?.loadedAdapterResponseInfo
                                val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                                val revenue = adValue.valueMicros.toDouble() / 1000000.0
                                adRevenue.setRevenue(revenue, adValue.currencyCode)
                                adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                                Adjust.trackAdRevenue(adRevenue)

                                val analytics = FirebaseAnalytics.getInstance(this@MainActivity)
                                val params = Bundle().apply {
                                    putString(
                                        FirebaseAnalytics.Param.AD_PLATFORM,
                                        "admob mediation"
                                    )
                                    putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                                    putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                                    putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                                    putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                                }
                                analytics.logEvent("ad_impression_2", params)
                            }
                    }
                }
            )
        }

        loadInterAds(0)
    }

    fun showInterstitial(onAdDismissedAction: () -> Unit) {
        if (!DeviceUtils.checkInternetConnection(mContext)) {
            onAdDismissedAction.invoke()
            return
        }
        val timeLoad = FirebaseRemoteConfig.getInstance()
            .getLong(RemoteConfigKey.INTER_DELAY)

        val timeSubtraction =
            Date().time - SharedPreferenceHelper.getLong(Constant.TIME_LOAD_NEW_INTER_ADS)
        if (timeSubtraction <= timeLoad) {
            onAdDismissedAction.invoke()
            return
        }

        if (mInterstitialAd == null) {
            if (adsConsentManager?.canRequestAds == false) {
                onAdDismissedAction.invoke()
                return
            }
            onAdDismissedAction.invoke()
            loadInterAd()
            return
        }
        mInterstitialAd?.show(mContext)

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                loadInterAd()
                SharedPreferenceHelper.storeLong(
                    Constant.TIME_LOAD_NEW_INTER_ADS,
                    Date().time
                )
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
                kotlin.runCatching {
                    onAdDismissedAction.invoke()
                }.onFailure {
                    it.printStackTrace()
                }
            }

            override fun onAdShowedFullScreenContent() {
                kotlin.runCatching {
                    onAdDismissedAction.invoke()
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    fun forceShowInterstitial(onAdDismissedAction: () -> Unit) {
        if (!DeviceUtils.checkInternetConnection(mContext)) {
            onAdDismissedAction.invoke()
            return
        }

        if (mInterstitialAd == null) {
            if (adsConsentManager?.canRequestAds == false) {
                onAdDismissedAction.invoke()
                return
            }
            onAdDismissedAction.invoke()
            loadInterAd()
            return
        }
        mInterstitialAd?.show(mContext)

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                loadInterAd()
                SharedPreferenceHelper.storeLong(
                    Constant.TIME_LOAD_NEW_INTER_ADS,
                    Date().time
                )
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
                kotlin.runCatching {
                    onAdDismissedAction.invoke()
                }.onFailure {
                    it.printStackTrace()
                }
            }

            override fun onAdShowedFullScreenContent() {
                kotlin.runCatching {
                    onAdDismissedAction.invoke()
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    companion object {
        const val ITEMS_PER_AD = 5
    }
}