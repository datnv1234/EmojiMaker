package com.wa.ai.emojimaker.utils.customview.customsticker;

import android.content.Context;
import android.view.MotionEvent;

import java.util.Objects;

public class CopyIconEvent implements StickerIconEvent {
    private final Context context;

    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
    }

    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
    }

    public CopyIconEvent(Context context2) {
        this.context = context2;
    }

    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        if (stickerView.isLockCurrent()) {
            return;
        }
        if (stickerView.getStickerCount() > 49) {
            Context context2 = this.context;
            //Toast.makeText(context2, context2.getResources().getString(R.string.more_than_50_items_at_once), 0).show();
            return;
        }
        stickerView.clone(Objects.requireNonNull(stickerView.getCurrentSticker()));
    }
}
