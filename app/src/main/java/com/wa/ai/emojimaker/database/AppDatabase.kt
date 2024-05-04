package com.wa.ai.emojimaker.database

import android.content.Context
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.wa.ai.emojimaker.data.local.dao.EmojiDao
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.Reflection

/*@Metadata(
    d1 = ["\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b'\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&¨\u0006\u000c"],
    d2 = ["Lcom/emojimaker/emojistitch/database/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "emojiDao", "Lcom/emojimaker/emojistitch/database/EmojiDao;", "packageNameDao", "Lcom/emojimaker/emojistitch/database/PackageDao;", "tagDao", "Lcom/emojimaker/emojistitch/database/TagDao;", "userRecordDao", "Lcom/emojimaker/emojistitch/database/UserRecordDao;", "Companion", "EmojiMaker1.0.3_04.03.2024_release"],
    k = 1,
    mv = [1, 7, 1],
    xi = 48
)*/
/* compiled from: AppDatabase.kt */
abstract class AppDatabase : RoomDatabase() {
    abstract fun emojiDao(): EmojiDao?
    abstract fun packageNameDao(): PackageDao?
    abstract fun tagDao(): TagDao?
    abstract fun userRecordDao(): UserRecordDao?

    /*@Metadata(
        d1 = ["\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000¨\u0006\b"],
        d2 = ["Lcom/emojimaker/emojistitch/database/AppDatabase\$Companion;", "", "()V", "INSTANCE", "Lcom/emojimaker/emojistitch/database/AppDatabase;", "getInstance", "context", "Landroid/content/Context;", "EmojiMaker1.0.3_04.03.2024_release"],
        k = 1,
        mv = [1, 7, 1],
        xi = 48
    ) *//* compiled from: AppDatabase.kt */
    class Companion private constructor() {
        /* synthetic */ constructor(defaultConstructorMarker: DefaultConstructorMarker?) : this()

        fun getInstance(context: Context): AppDatabase {
            Intrinsics.checkNotNullParameter(context, "context")
            if (AppDatabase.Companion.INSTANCE == null) {
                synchronized(
                    Reflection.getOrCreateKotlinClass(
                        AppDatabase::class.java
                    )
                ) {
                    val companion: Companion = AppDatabase.Companion.Companion
                    AppDatabase.Companion.INSTANCE = databaseBuilder<AppDatabase>(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "AppDatabase"
                    ).allowMainThreadQueries().build()
                    val unit = Unit
                }
            }
            return AppDatabase.Companion.INSTANCE
        }
    }

    companion object {
        val Companion = Companion(null as DefaultConstructorMarker?)

        /* access modifiers changed from: private */
        var INSTANCE: AppDatabase? = null
    }
}
