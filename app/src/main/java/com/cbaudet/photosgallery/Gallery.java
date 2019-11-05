package com.cbaudet.photosgallery;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class Gallery extends View {

    private final int MAX_IMAGE_ROW = 7;
    private final int MIN_IMAGE_ROW = 1;
    private final float MARGIN = 10;

    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private Paint mPaint;
    private DisplayMetrics mDisplayMetrics;
    private float mY = 0;

    private List<Bitmap> mImages = new ArrayList<>();

    public Gallery(Context context) {
        super(context);

        mDisplayMetrics = getResources().getDisplayMetrics();

        mPaint = new Paint();

        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());
    }

    public void setImages(List<Bitmap> images) {
        this.mImages = images;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int itemPerRow = Math.max(Math.round(MAX_IMAGE_ROW / mScale), MIN_IMAGE_ROW);

        float imageWidth = (mDisplayMetrics.widthPixels - ((itemPerRow + 1 ) * MARGIN))/itemPerRow;
        double nbRow = Math.ceil(this.mImages.size() / itemPerRow);

        for (int i = 0; i < nbRow; i++) {
            for (int j = 0; j < itemPerRow; j++) {
                int index = (i * itemPerRow) + j;
                float x = j * MARGIN + (j-1) * imageWidth;
                float y = mY + (i * MARGIN + (i-1) * imageWidth);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(mImages.get(index), (int) imageWidth, (int) imageWidth, false);
                canvas.drawBitmap(scaledBitmap, x, y, mPaint);

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                mY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                // IDK ?
                //invalidate();
                break;
        }
        return true;
    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            invalidate();
            return true;
        }
    }
}
