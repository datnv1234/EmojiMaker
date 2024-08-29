package com.wa.ai.emojimaker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wa.ai.emojimaker.data.model.CollectionUI
import com.wa.ai.emojimaker.database.dao.CollectionDao
import com.wa.ai.emojimaker.utils.Converters

@Database(
    entities = [CollectionUI::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun collectionDao(): CollectionDao

    companion object {
        val DATABASE_NAME: String = "DATABASE_NAME"
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {

            }
        }

    }
}