package com.wa.ai.emojimaker.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
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
    fun provideSharedPreference(context: Application?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }


//    @Singleton
//    @Provides
//    fun provideNotificationHelper(application: Application): NotificationHelper {
//        return NotificationHelper()
//    }

//    @Provides
//    @Singleton
//    fun provideRoomDb3(context: Application): ReflectTVDatabase {
//        return Room.databaseBuilder(
//            context,
//            ReflectTVDatabase::class.java,
//            Constant.DATABASE_NAME
//        )
//            .allowMainThreadQueries()
//            .build()
//    }

}