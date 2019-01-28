package com.tuanfadbg.takephotoutils;

public interface TakePhotoCallback {
    void onSuccess(String path, int width, int height);

    void onFail();
}