package com.cbaudet.photosgallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Bitmap> images;

    Gallery galleryView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        images = new ArrayList<>();
        galleryView = new Gallery(this);
        setContentView(galleryView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        galleryView.setImages(images);
    }
}
