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

/**
 * Composant principal de l'application, c'est cette vue qui est chargée d'afficher toute la gallerie
 */
public class Gallery extends View {

    public final int MAX_IMAGE_ROW = 7;
    private final int MIN_IMAGE_ROW = 1;
    private final int MARGIN = 10;

    private int imageWidth = 0;
    private int nbRow = 0;

    private float mScale = 1f;
    private GestureDetector mScrollGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private DisplayMetrics mDisplayMetrics;

    private List<Bitmap[]> mImages = new ArrayList<>();
    int startY = 0;

    public Gallery(Context context) {
        super(context);

        mDisplayMetrics = getResources().getDisplayMetrics();

        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());
        mScrollGestureDetector = new GestureDetector(context, new ScrollGesture());
    }

    /**
     *
     * @param images Liste de samples de bitmaps en fonction du nombre d'image par ligne
     */
    public void setImages(List<Bitmap[]> images) {
        this.mImages = images;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = this.mImages.size();

        int itemPerRow = Math.max(Math.round(MAX_IMAGE_ROW / Math.max(mScale, 1)), MIN_IMAGE_ROW);
        this.imageWidth = (int) Math.floor((mDisplayMetrics.widthPixels - ((itemPerRow + 1) * MARGIN))/itemPerRow);
        this.nbRow = (int) Math.ceil(size / (double) itemPerRow);

        for (int i = 0; i < nbRow; i++) {
            // Pour afficher le bon nombre d'image sur la dernière ligne
            int itemForCurrentRow = i == nbRow - 1 ? (size % itemPerRow == 0 ? itemPerRow : size % itemPerRow) : itemPerRow;
            for (int j = 0; j < itemForCurrentRow; j++) {
                int index = (i * itemPerRow) + j;
                float x = (j+1) * MARGIN + j * imageWidth;
                float y = startY + ((i+1) * MARGIN) + (i * imageWidth);
                Bitmap b = Bitmap.createScaledBitmap(mImages.get(index)[itemPerRow - 1], imageWidth, imageWidth, true);
                canvas.drawBitmap(b, x, y, null);
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
            //boolean canGoesDown = startY > (((nbRow * imageWidth) + ((nbRow+1)*MARGIN)) * -1);
            startY -= distanceY;
            if(startY-distanceY > MARGIN) {
                startY = MARGIN;
            }
            invalidate();
            return true;
        }
    }
}
