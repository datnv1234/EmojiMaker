package com.wa.ai.emojimaker.data.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StickerPackView(
    val androidPlayStoreLink: String? = null,
    val iosAppStoreLink: String?,
    val publisherEmail: String?,
    val privacyPolicyWebsite: String?,
    val licenseAgreementWebsite: String?,
    val telegram_url: String,
    val identifier: Int,
    val name: String,
    val publisher: String,
    val publisherWebsite: String,
    val animatedStickerPack: Boolean,
    val stickers: List<Sticker>,
    val trayImageFile: String
) : Serializable, Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
    }
}