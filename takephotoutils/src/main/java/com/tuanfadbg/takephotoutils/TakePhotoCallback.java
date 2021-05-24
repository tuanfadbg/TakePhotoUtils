package com.tuanfadbg.takephotoutils;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public interface TakePhotoCallback {

    void onMultipleSuccess(List<String> imagesEncodedList, ArrayList<Uri> mArrayUri, List<Long> lastModifieds);

    void onSuccess(String path, Bitmap bitmap, int width, int height, Uri sourceUri, long lastModified);

//    void onSuccess(String path, int width, int height);

    void onFail();
}