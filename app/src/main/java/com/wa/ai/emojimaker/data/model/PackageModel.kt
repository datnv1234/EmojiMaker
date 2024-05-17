package com.wa.ai.emojimaker.data.model

import android.graphics.Bitmap

data class PackageModel(val name: String, var itemSize: Int = 0, val avatar: Bitmap? = null) {
    val id: String = setId()

    private fun setId(): String {
        return name.replace(" ", "_")
    }

}
