package com.wa.ai.emojimaker.data.repo.emoji

import android.app.Activity
import com.wa.ai.emojimaker.data.model.EmojiUI
import kotlinx.coroutines.flow.Flow

interface EmojiRepo {
    suspend fun getAllEmoji(activity: Activity): Flow<List<EmojiUI>>

}