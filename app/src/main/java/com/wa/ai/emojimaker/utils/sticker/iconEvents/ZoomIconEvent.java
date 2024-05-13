package com.wa.ai.emojimaker.utils.sticker.iconEvents;

import android.view.MotionEvent;

import com.wa.ai.emojimaker.utils.sticker.StickerIconEvent;
import com.wa.ai.emojimaker.utils.sticker.StickerView;
import com.wa.ai.emojimaker.utils.sticker.StickerViewModel;

/**
 * @author wa
 */

public class ZoomIconEvent implements StickerIconEvent {
    @Override
    public void onActionDown(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {
        viewModel.zoomAndRotateCurrentSticker(event);
    }

    @Override
    public void onActionUp(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {
    }

}
