package com.wa.ai.emojimaker.utils.customview.customsticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class Sticker {
    private final float[] boundPoints = new float[8];
    private boolean dirty;
    private boolean isFlippedHorizontally;
    private boolean isFlippedVertically;
    private boolean isHide;
    private boolean isLock;
    private final float[] mappedBounds = new float[8];
    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];
    private int pagerSelect;
    private int posSelect;
    private final RectF trappedRect = new RectF();
    private final float[] unrotatedPoint = new float[2];
    private final float[] unrotatedWrapperCorner = new float[8];

    @Retention(RetentionPolicy.SOURCE)
    public @interface Position {
        public static final int BOTTOM = 16;
        public static final int CENTER = 1;
        public static final int LEFT = 4;
        public static final int RIGHT = 8;
        public static final int TOP = 2;
    }

    public abstract void draw(Canvas canvas);

    public abstract Drawable getDrawable();

    public abstract String getDrawablePath();

    public abstract int getHeight();

    public abstract int getWidth();

    public void release() {
    }

    public abstract Sticker setAlpha(int i2);

    public abstract Sticker setDrawable(Drawable drawable);

    public void markAsDirty() {
        this.dirty = true;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public int getPagerSelect() {
        return this.pagerSelect;
    }

    public Sticker setPagerSelect(int i2) {
        this.pagerSelect = i2;
        return this;
    }

    public int getPosSelect() {
        return this.posSelect;
    }

    public Sticker setPosSelect(int i2) {
        this.posSelect = i2;
        return this;
    }

    public boolean isLock() {
        return this.isLock;
    }

    public Sticker setLock(boolean z) {
        this.isLock = z;
        return this;
    }

    public boolean isHide() {
        return this.isHide;
    }

    public Sticker setHide(boolean z) {
        this.isHide = z;
        return this;
    }

    public boolean isFlippedHorizontally() {
        return this.isFlippedHorizontally;
    }

    public Sticker setFlippedHorizontally(boolean z) {
        this.isFlippedHorizontally = z;
        return this;
    }

    public boolean isFlippedVertically() {
        return this.isFlippedVertically;
    }

    public Sticker setFlippedVertically(boolean z) {
        this.isFlippedVertically = z;
        return this;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public Sticker setMatrix(Matrix matrix2) {
        markAsDirty();
        this.matrix.set(matrix2);
        return this;
    }

    public float[] getBoundPoints() {
        float[] fArr = new float[8];
        getBoundPoints(fArr);
        return fArr;
    }

    public void getBoundPoints(float[] fArr) {
        if (!this.isFlippedHorizontally) {
            if (!this.isFlippedVertically) {
                fArr[0] = 0.0f;
                fArr[1] = 0.0f;
                fArr[2] = (float) getWidth();
                fArr[3] = 0.0f;
                fArr[4] = 0.0f;
                fArr[5] = (float) getHeight();
                fArr[6] = (float) getWidth();
                fArr[7] = (float) getHeight();
                return;
            }
            fArr[0] = 0.0f;
            fArr[1] = (float) getHeight();
            fArr[2] = (float) getWidth();
            fArr[3] = (float) getHeight();
            fArr[4] = 0.0f;
            fArr[5] = 0.0f;
            fArr[6] = (float) getWidth();
            fArr[7] = 0.0f;
        } else if (!this.isFlippedVertically) {
            fArr[0] = (float) getWidth();
            fArr[1] = 0.0f;
            fArr[2] = 0.0f;
            fArr[3] = 0.0f;
            fArr[4] = (float) getWidth();
            fArr[5] = (float) getHeight();
            fArr[6] = 0.0f;
            fArr[7] = (float) getHeight();
        } else {
            fArr[0] = (float) getWidth();
            fArr[1] = (float) getHeight();
            fArr[2] = 0.0f;
            fArr[3] = (float) getHeight();
            fArr[4] = (float) getWidth();
            fArr[5] = 0.0f;
            fArr[6] = 0.0f;
            fArr[7] = 0.0f;
        }
    }

    public float[] getMappedBoundPoints() {
        float[] fArr = new float[8];
        getMappedPoints(fArr, getBoundPoints());
        return fArr;
    }

    public float[] getMappedPoints(float[] fArr) {
        float[] fArr2 = new float[fArr.length];
        this.matrix.mapPoints(fArr2, fArr);
        return fArr2;
    }

    public void getMappedPoints(float[] fArr, float[] fArr2) {
        this.matrix.mapPoints(fArr, fArr2);
    }

    public RectF getBound() {
        RectF rectF = new RectF();
        getBound(rectF);
        return rectF;
    }

    public void getBound(RectF rectF) {
        rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
    }

    public RectF getMappedBound() {
        RectF rectF = new RectF();
        getMappedBound(rectF, getBound());
        return rectF;
    }

    public void getMappedBound(RectF rectF, RectF rectF2) {
        this.matrix.mapRect(rectF, rectF2);
    }

    public PointF getCenterPoint() {
        PointF pointF = new PointF();
        getCenterPoint(pointF);
        return pointF;
    }

    public void getCenterPoint(PointF pointF) {
        pointF.set((((float) getWidth()) * 1.0f) / 2.0f, (((float) getHeight()) * 1.0f) / 2.0f);
    }

    public PointF getMappedCenterPoint() {
        PointF centerPoint = getCenterPoint();
        getMappedCenterPoint(centerPoint, new float[2], new float[2]);
        return centerPoint;
    }

    public void getMappedCenterPoint(PointF pointF, float[] fArr, float[] fArr2) {
        getCenterPoint(pointF);
        fArr2[0] = pointF.x;
        fArr2[1] = pointF.y;
        getMappedPoints(fArr, fArr2);
        pointF.set(fArr[0], fArr[1]);
    }

    public float getCurrentScale() {
        return getMatrixScale(this.matrix);
    }

    public float getCurrentHeight() {
        return getMatrixScale(this.matrix) * ((float) getHeight());
    }

    public float getCurrentWidth() {
        return getMatrixScale(this.matrix) * ((float) getWidth());
    }

    public float getMatrixScale(Matrix matrix2) {
        return (float) Math.sqrt(Math.pow((double) getMatrixValue(matrix2, 0), 2.0d) + Math.pow((double) getMatrixValue(matrix2, 3), 2.0d));
    }

    public float getCurrentAngle() {
        return getMatrixAngle(this.matrix);
    }

    public float getMatrixAngle(Matrix matrix2) {
        return (float) Math.toDegrees(-Math.atan2((double) getMatrixValue(matrix2, 1), (double) getMatrixValue(matrix2, 0)));
    }

    public float getMatrixValue(Matrix matrix2, int i2) {
        matrix2.getValues(this.matrixValues);
        return this.matrixValues[i2];
    }

    public boolean contains(float f2, float f3) {
        return contains(new float[]{f2, f3});
    }

    public boolean contains(float[] fArr) {
        Matrix matrix2 = new Matrix();
        matrix2.setRotate(-getCurrentAngle());
        getBoundPoints(this.boundPoints);
        getMappedPoints(this.mappedBounds, this.boundPoints);
        matrix2.mapPoints(this.unrotatedWrapperCorner, this.mappedBounds);
        matrix2.mapPoints(this.unrotatedPoint, fArr);
        StickerUtils.trapToRect(this.trappedRect, this.unrotatedWrapperCorner);
        RectF rectF = this.trappedRect;
        float[] fArr2 = this.unrotatedPoint;
        return rectF.contains(fArr2[0], fArr2[1]);
    }
}
