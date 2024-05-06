package com.wa.ai.emojimaker.utils.sticker.iconEvents;

import android.view.MotionEvent;

import com.wa.ai.emojimaker.utils.sticker.StickerIconEvent;
import com.wa.ai.emojimaker.utils.sticker.StickerView;
import com.wa.ai.emojimaker.utils.sticker.StickerViewModel;

/**
 * @author wupanjie
 */

public class CropIconEvent implements StickerIconEvent {
    private final int gravity;

    public CropIconEvent(int gravity) {
        this.gravity = gravity;
    }

    @Override
    public void onActionDown(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {
       viewModel.cropCurrentSticker(event, gravity);
    }

    @Override
    public void onActionUp(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {
    }

}
