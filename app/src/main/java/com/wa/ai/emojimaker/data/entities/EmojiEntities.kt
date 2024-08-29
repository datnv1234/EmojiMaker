package com.wa.ai.emojimaker.data.entities

import com.wa.ai.emojimaker.data.model.EmojiUI

data class EmojiEntities(
    val emojiName: String,
    val date: String,
    val emojiUnicode: String
)

fun EmojiEntities.toEmojiUI(): EmojiUI {
    return EmojiUI(emojiName = emojiName, date = date, emojiUnicode = emojiUnicode)
}