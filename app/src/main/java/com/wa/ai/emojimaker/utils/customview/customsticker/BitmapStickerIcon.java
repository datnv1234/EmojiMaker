package com.wa.ai.emojimaker.utils.customview.customsticker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BitmapStickerIcon extends DrawableSticker implements StickerIconEvent {
    public static final float DEFAULT_ICON_EXTRA_RADIUS = 10.0f;
    public static final float DEFAULT_ICON_RADIUS = 35.0f;
    public static final int LEFT_BOTTOM = 2;
    public static final int LEFT_TOP = 0;
    public static final int RIGHT_BOTOM = 3;
    public static final int RIGHT_TOP = 1;
    private StickerIconEvent iconEvent;
    private float iconExtraRadius = 10.0f;
    private float iconRadius = 35.0f;
    private int position = 0;
    private float x;
    private float y;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Gravity {
    }

    public BitmapStickerIcon(Drawable drawable, int i2) {
        super(drawable, "no");
        this.position = i2;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(-1);
        canvas.drawCircle(this.x, this.y, this.iconRadius, paint);
        super.draw(canvas);
    }

    public float getX() {
        return this.x;
    }

    public void setX(float f2) {
        this.x = f2;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float f2) {
        this.y = f2;
    }

    public float getIconRadius() {
        return this.iconRadius;
    }

    public void setIconRadius(float f2) {
        this.iconRadius = f2;
    }

    public float getIconExtraRadius() {
        return this.iconExtraRadius;
    }

    public void setIconExtraRadius(float f2) {
        this.iconExtraRadius = f2;
    }

    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
        StickerIconEvent stickerIconEvent = this.iconEvent;
        if (stickerIconEvent != null) {
            stickerIconEvent.onActionDown(stickerView, motionEvent);
        }
    }

    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
        StickerIconEvent stickerIconEvent = this.iconEvent;
        if (stickerIconEvent != null) {
            stickerIconEvent.onActionMove(stickerView, motionEvent);
        }
    }

    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        StickerIconEvent stickerIconEvent = this.iconEvent;
        if (stickerIconEvent != null) {
            stickerIconEvent.onActionUp(stickerView, motionEvent);
        }
    }

    public StickerIconEvent getIconEvent() {
        return this.iconEvent;
    }

    public void setIconEvent(StickerIconEvent stickerIconEvent) {
        this.iconEvent = stickerIconEvent;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int i2) {
        this.position = i2;
    }
}
