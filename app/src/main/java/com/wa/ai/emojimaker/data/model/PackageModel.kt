package com.wa.ai.emojimaker.data.model

import android.graphics.Bitmap

data class PackageModel(val name: String, val avatar: Bitmap? = null) {
    val id: String = setId()

    private fun setId() : String{
        val id = name.lowercase()
        id.replace(" ", "_")
        return id
    }

}
