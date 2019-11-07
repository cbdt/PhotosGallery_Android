package com.cbaudet.photosgallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe gère la récupération des images
 */
public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static int DISPLAY_WIDTH;

    List<Bitmap[]> images;

    Gallery galleryView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        images = new ArrayList<>();
        galleryView = new Gallery(this);
        setContentView(galleryView);
        
        DISPLAY_WIDTH = getDisplayWidth();

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getPictures();
        galleryView.setImages(images);

    }

    /**
     * Permet de récupérer les images du téléphones dans plusieurs tailles diffrentes
     */
    private void getPictures(){
        Uri allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = this.getContentResolver().query(allImagesuri, projection, null, null, null);

        if(cursor != null) {
            while (cursor.moveToNext()) {
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                Bitmap[] bitmaps = getListResizedPicture(datapath);
                images.add(bitmaps);
            }
            cursor.close();
        }
    }

    /**
     * Permet de connaitre la largeur de l'écran
     * (Pas besoin d'afficher une image avec trop de pixels si on ne voit pas la difference sur l'écran)
     */
    private int getDisplayWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    /**
     * Calcule le nombre de fois qu'on divise la hauteur et la largeur de l'image
     * On choisit de diviser au maximum tout en ayant plus de pixels de largeur que disponible
     * pour ne pas avoir une image floue
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int maxWidth) {
        // Raw width of image
        final int width = options.outWidth;
        int inSampleSize = Math.max(Math.round(width/maxWidth),1);
        return inSampleSize;
    }

    /**
     * Renvoie le bitmap bien redimmensionné
     */
    private Bitmap decodeSampledBitmapFromFile(String datapath, int maxWidth) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(datapath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxWidth);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(datapath, options);
    }

    /**
     * Renvoie une liste avec les differentes tailles d'images
     * La taille disponible pour une image diminue en fonction du nombre d'images dans la ligne
     */
    private Bitmap[] getListResizedPicture(String datapath){
        Bitmap[] resizedPictures = new Bitmap[galleryView.MAX_IMAGE_ROW];
        for (int i=1;i<=galleryView.MAX_IMAGE_ROW;i++){
            resizedPictures[i-1] = decodeSampledBitmapFromFile(datapath,DISPLAY_WIDTH/i);
        }
        return resizedPictures;
    }

}
