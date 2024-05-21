package com.wa.ai.emojimaker.data.model

import android.graphics.Bitmap
import java.util.Objects

class Category(var category: String?,
               var categoryName: String?,
               var itemSize: Int = 0,
               var avatar1: Bitmap? = null,
               var avatar2: Bitmap? = null,
               var avatar3: Bitmap? = null,
               var avatar4: Bitmap? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if ((other == null) || (javaClass != other.javaClass)) {
            return false
        }
        val obj = other as Category
        return Objects.equals(category, obj.category)
    }

    override fun hashCode(): Int {
        return category?.hashCode() ?: 0
    }
}