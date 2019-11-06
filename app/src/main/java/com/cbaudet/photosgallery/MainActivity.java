package com.cbaudet.photosgallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    List<Bitmap> images;

    Gallery galleryView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        images = new ArrayList<>();
        galleryView = new Gallery(this);
        setContentView(galleryView);


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
                Bitmap b = BitmapFactory.decodeFile(datapath);
                images.add(b);
            }
            cursor.close();
        }

    }
}
