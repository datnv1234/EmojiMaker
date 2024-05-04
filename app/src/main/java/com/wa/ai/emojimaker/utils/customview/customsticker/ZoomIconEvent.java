package com.wa.ai.emojimaker.utils.customview.customsticker;

import android.view.MotionEvent;

public class ZoomIconEvent implements StickerIconEvent {
    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
    }

    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
        stickerView.zoomAndRotateCurrentSticker(motionEvent);
    }

    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        if (stickerView.getOnStickerOperationListener() != null) {
            stickerView.getOnStickerOperationListener().onStickerZoomFinished(stickerView.getCurrentSticker());
        }
    }
}
