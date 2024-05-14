package com.wa.ai.emojimaker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wa.ai.emojimaker.data.model.StickerModel

@Dao
interface StickerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSticker(stickerModel: StickerModel)

    @Update
    suspend fun update(stickerModel: StickerModel)

    @Delete
    suspend fun delete(stickerModel: StickerModel)

    @Query("SELECT * FROM stickers WHERE category = :category")
    fun getStickersByCategory(category: String): LiveData<List<StickerModel>>

    @Query("SELECT * FROM stickers")
    fun getAllStickers(): LiveData<List<StickerModel>>

    @Query("SELECT DISTINCT category FROM stickers")
    fun getAllCategories(): LiveData<List<String>>
}