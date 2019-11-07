package com.cbaudet.photosgallery;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class Gallery extends View {

    private final int MAX_IMAGE_ROW = 7;
    private final int MIN_IMAGE_ROW = 1;
    private final int MARGIN = 10;

    private float mScale = 1f;
    private GestureDetector mScrollGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private Paint mPaint;
    private DisplayMetrics mDisplayMetrics;

    int statusBarHeight = 0;

    private List<List<Bitmap>> mImages = new ArrayList<>();
    int startY = 0;

    public Gallery(Context context) {
        super(context);

        mDisplayMetrics = getResources().getDisplayMetrics();

        mPaint = new Paint();

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        int[] location = new int[2];
        getLocationInWindow(location);
        startY = location[1];


        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());
        mScrollGestureDetector = new GestureDetector(context, new ScrollGesture());
    }

    public void setImages(List<List<Bitmap>> images) {
        this.mImages = images;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = this.mImages.size();

        int itemPerRow = Math.max(Math.round(MAX_IMAGE_ROW / Math.max(mScale, 1)), MIN_IMAGE_ROW);
        int imageWidth = (int) Math.floor((mDisplayMetrics.widthPixels - ((itemPerRow + 1) * MARGIN))/itemPerRow);
        int nbRow = (int) Math.ceil(size / (double) itemPerRow);

        for (int i = 0; i < nbRow; i++) {
            int itemForCurrentRow = i == nbRow - 1 ? (size % itemPerRow == 0 ? itemPerRow : size % itemPerRow) : itemPerRow;
            for (int j = 0; j < itemForCurrentRow; j++) {
                int index = (i * itemPerRow) + j;
                float x = (j+1) * MARGIN + j * imageWidth;
                float y = startY + ((i+1) * MARGIN) + (i * imageWidth);
                Bitmap b = Bitmap.createScaledBitmap(mImages.get(index).get(6), imageWidth, imageWidth, true);
                canvas.drawBitmap(b, x, y, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mScrollGestureDetector.onTouchEvent(event);
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

    public class ScrollGesture extends  GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            startY -= distanceY;
            if(startY > MARGIN) {
                startY = MARGIN;
            }
            invalidate();
            return true;
        }
    }
}
