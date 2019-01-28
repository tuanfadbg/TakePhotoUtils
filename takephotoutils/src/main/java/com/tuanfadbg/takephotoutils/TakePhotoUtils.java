package com.tuanfadbg.takephotoutils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class TakePhotoUtils {
    private Activity activity;

    private static final int READ_EXTERNAL_REQUEST_CODE = 110;

    private static final int RESULT_LOAD_IMG = 111;
    private static final int CAMERA_REQUEST_CODE = 112;
    private static final int WRITE_EXTERNAL_REQUEST_CODE = 113;
    private boolean isCamera;
    private String authority;
    private TakePhotoCallback takePhotoCallback;

    private boolean isPortrait = false;
    private int resizeWidth = 0, resizeHeight = 0, maxSide = 0, quality = 100;
    private boolean isHasOptions = false;

    private Bitmap resultBitmap = null;
    private int imageWidth, imageHeight;

    private String resultImagePath = "";

    public TakePhotoUtils(Activity activity, String authority) {
        this.activity = activity;
        this.authority = authority;
    }

    public TakePhotoUtils showDialogSelectImage() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(activity.getString(R.string.camera));
        arrayAdapter.add(activity.getString(R.string.gallery));

        builderSingle.setNegativeButton(activity.getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            if (which == 0) {
                checkPermisstionAndstartIntentTakePhoto();
            } else {
                checkPermisstionAndBrowserImage();
            }
        });
        builderSingle.show();
        return this;
    }

    public void setListener(TakePhotoCallback takePhotoCallback) {
        this.takePhotoCallback = takePhotoCallback;
    }

    private TakePhotoUtils getImageFromGallery() {
        checkPermisstionAndBrowserImage();
        return this;
    }

    private TakePhotoUtils takePhoto() {
        checkPermisstionAndstartIntentTakePhoto();
        return this;
    }

    private void checkPermisstionAndBrowserImage() {
        if (isReadExternalPermissionGranted()) {
            showBrowserImage();
        }
    }

    private void checkPermisstionAndstartIntentTakePhoto() {
        if (isCameraPermissionGranted()) {
            intentTakePhoto();
        }
    }

    private boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private boolean isWriteExternalPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private boolean isReadExternalPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_REQUEST_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showBrowserImage();
                }
                break;
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isWriteExternalPermissionGranted()) {
                        intentTakePhoto();
                    }
                }
                break;
            case WRITE_EXTERNAL_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intentTakePhoto();
                }
                break;
        }
    }

    private void intentTakePhoto() {
        isCamera = true;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        authority,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, RESULT_LOAD_IMG);
            }
        }
    }

    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File createImageFileResize() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyy`yMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName + "resize",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showBrowserImage() {
        isCamera = false;
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhoto, RESULT_LOAD_IMG);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG) {

            if (isCamera) {
                setResultImagePath(mCurrentPhotoPath, null);
            } else if (data.getData() != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    setResultImagePath(null, selectedImage);
                }
            }

            decodeFileAndGetInfomation();

            if (isHasOptions) {
                if (resultBitmap == null) {
                    if (takePhotoCallback != null)
                        takePhotoCallback.onFail();
                    return;
                }

                if (maxSide != 0) {
                    resizeBitmapToMaxSide();
                } else if (resizeWidth != 0 && resizeHeight != 0) {
                    resizeImageToFixSize();
                }

                if (isPortrait) {
                    convertImageToPortrait();
                }

                if (isCamera)
                    removeOldFile();

                File file = null;
                try {
                    file = createImageFileResize();
                    resultImagePath = mCurrentPhotoPath;
                } catch (IOException e) {
                    if (takePhotoCallback != null)
                        takePhotoCallback.onFail();
                    e.printStackTrace();
                }
                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(file);
                    resultBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
                    fOut.flush();
                    fOut.close();
                    resultBitmap.recycle();
                } catch (Exception e) {
                    if (takePhotoCallback != null)
                        takePhotoCallback.onFail();
                }


            }

            if (takePhotoCallback != null) {
                takePhotoCallback.onSuccess(resultImagePath, imageWidth, imageHeight);
            }

            if (isHasOptions) {
                removeAllOptions();
            }
        }
    }

    private void decodeFileAndGetInfomation() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resultImagePath, options);
        resultBitmap = BitmapFactory.decodeFile(resultImagePath);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
    }

    private void removeOldFile() {
        File oldFile = new File(resultImagePath);
        if (oldFile.exists()) {
            oldFile.delete();
        }
    }


    private void convertImageToPortrait() {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(resultImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) imageWidth / 2, (float) imageHeight / 2);

        resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, imageWidth, imageHeight, matrix, true);
        int temp = imageHeight;
        imageHeight = imageWidth;
        imageWidth = temp;
    }

    private void resizeImageToFixSize() {
        resultBitmap = Bitmap.createScaledBitmap(resultBitmap, resizeWidth, resizeHeight, true);
        imageWidth = resizeWidth;
        imageHeight = resizeHeight;
    }

    private void resizeBitmapToMaxSide() {
        if (imageHeight > maxSide || imageWidth > maxSide) {
            int newImageHeight;
            int newImageWidth;
            if (imageHeight > imageWidth) {
                float scale = (float) maxSide / imageHeight;
                newImageHeight = maxSide;
                newImageWidth = (int) (scale * (float) imageWidth);
            } else {
                float scale = (float) maxSide / imageWidth;
                newImageWidth = maxSide;
                newImageHeight = (int) (scale * (float) imageHeight);
            }
            imageHeight = newImageHeight;
            imageWidth = newImageWidth;
            resultBitmap = Bitmap.createScaledBitmap(resultBitmap, imageWidth, imageHeight, true);
        }
    }

    public TakePhotoUtils toPortrait() {
        isPortrait = true;
        isHasOptions = true;
        return this;
    }

    public TakePhotoUtils resize(int width, int height) {
        this.resizeWidth = width;
        this.resizeHeight = height;
        isHasOptions = true;
        return this;
    }

    public TakePhotoUtils resizeToMaxSide(int maxSide) {
        this.maxSide = maxSide;
        isHasOptions = true;
        return this;
    }

    public TakePhotoUtils quanlity(int quanlity) {
        if (quanlity < 0 || quanlity > 100)
            this.quality = 100;
        else
            this.quality = quanlity;
        isHasOptions = true;
        return this;
    }

    private void removeAllOptions() {
        isPortrait = false;
        resizeWidth = 0;
        resizeHeight = 0;
        maxSide = 0;
        isHasOptions = false;
        resultImagePath = "";
    }

    private void setResultImagePath(String absolutePath, Uri uri) {
        if (absolutePath == null)
            absolutePath = Utils.getPathFromUri(activity, uri);
        resultImagePath = absolutePath;
    }

}
