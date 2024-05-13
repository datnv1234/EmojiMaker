package com.wa.ai.emojimaker.utils.sticker.iconEvents;

import android.view.MotionEvent;
import android.widget.Toast;

import com.wa.ai.emojimaker.utils.sticker.StickerIconEvent;
import com.wa.ai.emojimaker.utils.sticker.StickerView;
import com.wa.ai.emojimaker.utils.sticker.StickerViewModel;

/**
 * @author wa
 */

public class DeleteIconEvent implements StickerIconEvent {
    @Override
    public void onActionDown(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {

    }

    @Override
    public void onActionUp(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {
        Toast.makeText(stickerView.getContext(), "Long press to delete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActionLongPress(StickerView stickerView, StickerViewModel viewModel, MotionEvent event) {
        viewModel.removeCurrentSticker();
    }
}
