package com.wa.ai.emojimaker.utils.ads

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.visible


class NativeAdsUtils {
    companion object {
        val instance: NativeAdsUtils by lazy { NativeAdsUtils() }
    }

    fun loadNativeAds(context: Context, keyAds: String, adsLoadCallBack: (NativeAd?) -> Unit) {
        val adLoader = AdLoader.Builder(context, keyAds)
            .forNativeAd { nativeAd ->
                adsLoadCallBack(nativeAd)
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    adsLoadCallBack(null)
                }

                override fun onAdLoaded() {
                    Log.d(TAG, "onAdLoaded: ")
                    super.onAdLoaded()
                }
            }).build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }


    fun populateNativeAdVideoView(nativeAd: NativeAd, nativeAdView: NativeAdView) {
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

            nativeAd.mediaContent?.let {
                adMedia?.mediaContent = it
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