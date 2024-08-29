package com.wa.ai.emojimaker.data.model

import android.graphics.Bitmap

data class PackageModel(private var name: String, var itemSize: Int = 0, val avatar: Bitmap? = null) {

    var id: String = setId()
        private set

    init {
        id = setId()
    }

    fun getName(): String {
        return name
    }

    private fun setId(): String {
        return getID(name)
    }

    fun updateName(newName: String) {
        name = newName
        id = setId()
    }

    companion object {
        fun getID(name: String) : String {
            return name.replace(" ", "_")
        }
    }

}
