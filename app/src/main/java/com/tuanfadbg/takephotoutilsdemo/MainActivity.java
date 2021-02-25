package com.tuanfadbg.takephotoutilsdemo;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tuanfadbg.takephotoutils.TakePhotoCallback;
import com.tuanfadbg.takephotoutils.TakePhotoUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    TakePhotoUtils takePhotoUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button3).setOnClickListener(v -> showDialog());

        takePhotoUtils = new TakePhotoUtils(this, getString(R.string.authority));
    }


    public void showDialog() {
        takePhotoUtils.showDialogSelectImage(null, null, null)

                .setListener(new TakePhotoCallback() {
                    @Override
                    public void onMultipleSuccess(List<String> imagesEncodedList, ArrayList<Uri> mArrayUri, List<Long> lastModifieds) {
                        Log.e(TAG, "onMultipleSuccess: " + imagesEncodedList.get(0) + " "
                                + mArrayUri.get(0).getPath() + " " + lastModifieds.get(0) );
                    }

                    @Override
                    public void onSuccess(Bitmap bitmap, int width, int height, Uri sourceUri, long lastModified) {
                        Log.e(TAG, "onSuccess 1: " + width + " " + height + " " + sourceUri.getPath() + " " + lastModified);
                    }

                    @Override
                    public void onSuccess(String path, int width, int height) {
                        Log.e(TAG, "onSuccess 2: " + width + " " + height + " " + path);
                    }

                    @Override
                    public void onFail() {
                        Log.e(TAG, "onFail: " );
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        takePhotoUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        takePhotoUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
