package com.tuanfadbg.takephotoutilsdemo;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tuanfadbg.takephotoutils.TakePhotoCallback;
import com.tuanfadbg.takephotoutils.TakePhotoUtils;

public class MainActivity extends AppCompatActivity {

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
                .toPortrait()
                .resize(200, 200)
                .resizeToMaxSide(100)
                .quality(100)
                .setListener(new TakePhotoCallback() {
                    @Override
                    public void onSuccess(String path, int width, int height) {

                    }

                    @Override
                    public void onFail() {

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
