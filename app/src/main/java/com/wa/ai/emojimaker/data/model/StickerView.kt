package com.wa.ai.emojimaker.data.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StickerView(
    val emojis: List<String>,
    val imageFile: String,
    val imageFileThum: String
) : Serializable, Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
    }
}