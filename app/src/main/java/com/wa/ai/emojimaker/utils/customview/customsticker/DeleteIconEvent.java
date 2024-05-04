package com.wa.ai.emojimaker.utils.customview.customsticker;

import android.view.MotionEvent;

public class DeleteIconEvent implements StickerIconEvent {
    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
    }

    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
    }

    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        if (!stickerView.isLockCurrent()) {
            stickerView.removeCurrentSticker();
        }
    }
}
