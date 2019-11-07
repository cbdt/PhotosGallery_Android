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

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static int DISPLAY_WIDTH;

    List<List<Bitmap>> images;

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

    private void getPictures(){
        Uri allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = this.getContentResolver().query(allImagesuri, projection, null, null, null);

        if(cursor != null) {
            while (cursor.moveToNext()) {
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                List<Bitmap> b = getListResizedPicture(datapath);
                images.add(b);
            }
            cursor.close();
        }
    }

    // permet de recup la largeur de l'écran car au final pas besoin
    // d'afficher une image avec plus de pixel qu'on en a de dispo
    private int getDisplayWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    // calcul le nombre de fois qu'on divise la hauteur et largeur
    // de l'image pour que ça largeur soit inferieur au maxWidth
    private int calculateInSampleSize(BitmapFactory.Options options, int maxWidth) {
        // Raw width of image
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > maxWidth) {

            final int dividedWidth=width;

            while ((dividedWidth / inSampleSize) >= maxWidth) {
                inSampleSize ++;
            }
        }
        Log.e("inSampleSize", "" + inSampleSize);

        return inSampleSize;
    }

    // renvoie le bitmap resizé
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

    // on construit une liste avec les differentes tailles (autant qu'il y a de possibilité donc ici 7)
    // sachant que la taille disponible pour l'image est de
    // (taille de l'ecran/nb d'image à afficher sur la ligne)
    private List<Bitmap> getListResizedPicture(String datapath){
        List<Bitmap> resizedPictures = new ArrayList<>();
        for (int i=1;i<=galleryView.MAX_IMAGE_ROW;i++){
            resizedPictures.add(decodeSampledBitmapFromFile(datapath,DISPLAY_WIDTH/i));
        }
        return resizedPictures;
    }

}
