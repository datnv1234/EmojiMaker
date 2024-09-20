package com.wa.ai.emojimaker.utils.ads

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.visible
import timber.log.Timber

class NativeAdsUtils {
    companion object {
        val instance: NativeAdsUtils by lazy { NativeAdsUtils() }
    }

    private lateinit var analytics: FirebaseAnalytics
    var loadNativeCount = 0

    private fun pushEvent(activity: Activity, nativeAd: NativeAd, adValue: AdValue) {
        val loadedAdapterResponseInfo: AdapterResponseInfo? =
            nativeAd.responseInfo?.loadedAdapterResponseInfo
        val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
        val revenue = adValue.valueMicros.toDouble() / 1000000.0
        adRevenue.setRevenue(
            revenue,
            adValue.currencyCode
        )
        adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
        Adjust.trackAdRevenue(adRevenue)
        analytics = FirebaseAnalytics.getInstance(activity)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "admob mediation")
        params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
        params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Native")
        params.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
        params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
        analytics.logEvent("ad_impression_2", params)
    }

    fun loadNativeAds(
        activity: Activity,
        keyAds: String,
        adsLoadCallBack: (NativeAd?) -> Unit,
        adsClicked: () -> Unit
    ) {
        val adLoader = AdLoader.Builder(activity, keyAds)
            .forNativeAd { nativeAd ->
                adsLoadCallBack(nativeAd)
                nativeAd.setOnPaidEventListener { adValue ->
                    pushEvent(activity, nativeAd, adValue)
                }
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadNativeCount++
                    if (loadNativeCount <= 5) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                loadNativeAds(activity, keyAds, adsLoadCallBack, adsClicked)
                            },
                            2000
                        )
                    } else {
                        adsLoadCallBack(null)
                    }
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    loadNativeCount = 0
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    adsClicked()
                }
            }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun populateNativeAdVideoView(
        nativeAd: NativeAd,
        nativeAdView: NativeAdView,
        isShowMedia: Boolean = true
    ) {
        kotlin.runCatching {
            val adHeadline: TextView? = nativeAdView.findViewById(R.id.ad_headline)
            val adBody: TextView? = nativeAdView.findViewById(R.id.ad_body)
            val adCallToAction: TextView? =
                nativeAdView.findViewById(R.id.ad_call_to_action)
            val adAppIcon: ImageView? = nativeAdView.findViewById(R.id.ad_app_icon)
            val adAdvertiser: TextView? = nativeAdView.findViewById(R.id.ad_advertiser)
            val adMedia: MediaView? = nativeAdView.findViewById(R.id.ad_media)
            adHeadline?.setBackgroundColor(Color.TRANSPARENT)
            adAdvertiser?.setBackgroundColor(Color.TRANSPARENT)
            // Set the media view.
            nativeAdView.mediaView = adMedia
            // Set other ad assets.
            nativeAdView.headlineView = adHeadline
            nativeAdView.bodyView = adBody
            nativeAdView.callToActionView = adCallToAction
            nativeAdView.iconView = adAppIcon
            nativeAdView.advertiserView = adAdvertiser

            // The headline and media content are guaranteed to be in every NativeAd.
            adHeadline?.text = nativeAd.headline

            if (isShowMedia) {
                nativeAd.mediaContent?.let {
                    adMedia?.mediaContent = it
                }
            }
            // These assets aren't guaranteed to be in every NativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adBody?.gone()
            } else {
                adBody?.visible()
                adBody?.text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adCallToAction?.gone()
            } else {
                adCallToAction?.visible()
                adCallToAction?.text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adAppIcon?.gone()
            } else {
                adAppIcon?.setImageDrawable(nativeAd.icon?.drawable)
                adAppIcon?.visible()
            }

            if (nativeAd.advertiser == null) {
                adAdvertiser?.gone()
            } else {
                adAdvertiser?.text = nativeAd.advertiser
                adAdvertiser?.visible()
            }
            nativeAdView.setNativeAd(nativeAd)
        }.onFailure { it.printStackTrace() }

    }
}