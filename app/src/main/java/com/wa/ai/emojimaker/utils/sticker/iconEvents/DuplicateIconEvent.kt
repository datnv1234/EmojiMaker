package com.wa.ai.emojimaker.utils.sticker.iconEvents

import android.view.MotionEvent
import com.wa.ai.emojimaker.utils.sticker.StickerIconEvent
import com.wa.ai.emojimaker.utils.sticker.StickerView
import com.wa.ai.emojimaker.utils.sticker.StickerViewModel

class DuplicateIconEvent : StickerIconEvent {
    override fun onActionDown(
        stickerView: StickerView,
        viewModel: StickerViewModel,
        event: MotionEvent
    ) {
    }

    override fun onActionMove(
        stickerView: StickerView,
        viewModel: StickerViewModel,
        event: MotionEvent
    ) {
    }

    override fun onActionUp(
        stickerView: StickerView,
        viewModel: StickerViewModel,
        event: MotionEvent
    ) {
        viewModel.duplicateCurrentSticker()
    }
}
