package com.wa.ai.emojimaker.database

import android.content.Context
import android.graphics.ImageDecoder
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wa.ai.emojimaker.data.dao.StickerDao
import com.wa.ai.emojimaker.data.model.StickerModel

@Database(
    entities = [StickerModel::class],
    version = 1,
    exportSchema = false
)
abstract class StickerDatabase : RoomDatabase() {

    abstract val stickerDao: StickerDao

    companion object {
        @Volatile
        private var INSTANCE: StickerDatabase? = null

        fun getDatabase(context: Context): StickerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StickerDatabase::class.java,
                    "sticker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}