package com.wa.ai.emojimaker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "language_table")
data class LanguageUI(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String?,
    val code: String?,
    val avatar: Int?,
    var isSelected: Boolean = false,
)


const val TYPE_DOWNLOADED = 0
const val TYPE_DOWNLOAD = 1
const val TYPE_DOWNLOADING = 2
