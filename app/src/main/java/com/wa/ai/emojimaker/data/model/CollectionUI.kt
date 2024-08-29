package com.wa.ai.emojimaker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CollectionUI(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val path: String,
    val path1: String,
    val path2: String,
)