package com.wa.ai.emojimaker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stickers")
data class StickerModel(
    @PrimaryKey(autoGenerate = false)
    val id:String,
    @ColumnInfo(name = "stickerData", typeAffinity = ColumnInfo.BLOB)
    val stickerData : ByteArray,
    @ColumnInfo(name = "category")
    val category : String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StickerModel

        if (id != other.id) return false
        return stickerData.contentEquals(other.stickerData)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + stickerData.contentHashCode()
        return result
    }
}
