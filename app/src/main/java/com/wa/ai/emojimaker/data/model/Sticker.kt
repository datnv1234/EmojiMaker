package com.wa.ai.emojimaker.data.model

data class Sticker(
    val emojis: List<String>?,
    val imageFile: String?,
    val imageFileThum: String?
) {

    fun toStickerView(): StickerView =
        StickerView(
            emojis ?: emptyList(),
            imageFile ?: "",
            imageFileThum ?: ""
        )

}