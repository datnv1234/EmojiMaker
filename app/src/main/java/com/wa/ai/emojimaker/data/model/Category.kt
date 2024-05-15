package com.wa.ai.emojimaker.data.model

import java.util.Objects

class Category(var category: String?, var categoryName: String?, var itemSize: Int = 0) {
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