package com.wa.ai.emojimaker.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.wa.ai.emojimaker.database.AppDatabase
import com.wa.ai.emojimaker.database.dao.CollectionDao
import com.wa.ai.emojimaker.data.repo.collection.CollectionRepo
import com.wa.ai.emojimaker.data.repo.collection.CollectionRepoImpl
import com.wa.ai.emojimaker.data.repo.emoji.EmojiRepo
import com.wa.ai.emojimaker.data.repo.emoji.EmojiRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSharedPreference(context: Application): SharedPreferences {
        return context.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRoomDb3(context: Application): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideCollectionDao(database: AppDatabase): CollectionDao {
        return database.collectionDao()
    }


    @Provides
    @Singleton
    fun provideCollectionRepo(collectionDao: CollectionDao): CollectionRepo {
        return CollectionRepoImpl(collectionDao)
    }

    @Provides
    @Singleton
    fun provideEmojiRepo(): EmojiRepo {
        return EmojiRepoImpl()
    }
}