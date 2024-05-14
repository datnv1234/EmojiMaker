package com.wa.ai.emojimaker.data.repo

import androidx.lifecycle.LiveData
import com.wa.ai.emojimaker.data.dao.StickerDao
import com.wa.ai.emojimaker.data.model.StickerModel

class StickerRepository(private val stickerDao: StickerDao) {
    suspend fun insert(stickerModel: StickerModel) {
        stickerDao.insertSticker(stickerModel)
    }

    suspend fun update(stickerModel: StickerModel) {
        stickerDao.update(stickerModel)
    }

    suspend fun delete(stickerModel: StickerModel) {
        stickerDao.delete(stickerModel)
    }

    fun getImagesByCategory(category: String): LiveData<List<StickerModel>> {
        return stickerDao.getStickersByCategory(category)
    }

    fun getAllSticker() : LiveData<List<StickerModel>> {
        return stickerDao.getAllStickers()
    }

    fun getAllCategories() : LiveData<List<String>> {
        return stickerDao.getAllCategories()
    }
}