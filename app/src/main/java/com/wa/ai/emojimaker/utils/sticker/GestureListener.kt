package com.wa.ai.emojimaker.utils.sticker

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent

class GestureListener(private val viewModel: StickerViewModel) : SimpleOnGestureListener() {
    var stickerView: StickerView? = null

    override fun onLongPress(event: MotionEvent) {
        if (viewModel.currentMode.value == StickerViewModel.ActionMode.ICON) {
            viewModel.currentIcon.value?.onActionLongPress(stickerView, viewModel, event)
        }
    }
}