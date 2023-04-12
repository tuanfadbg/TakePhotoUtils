# Take Photo Utils

A simple library helps you to take photo full size and pick a photo from the gallery, storage in Android.
Resize image, change quality after taking or pick.

## Installing
**Step 1:** Add it in your root build.gradle: (Project) at the end of repositories:

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
 
 
**Step 2:** Add the dependency in build.gradle (Module:app)

```
dependencies {
  ...
  implementation 'com.github.tuanfadbg:TakePhotoUtils:2.2.13'
}
```

## How to use

Replace `com.tuanfadbg.takephotoutilsdemo` with your package

Manifest.xml
```
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="29"/>
 <!-- Required only if your app needs to access images or photos
         that other apps created. -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="com.tuanfadbg.takephotoutilsdemo.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
</provider>
...
	<queries>
		<!-- Browser -->
		<intent>
		    <action android:name="android.intent.action.VIEW" />
		    <data android:scheme="http" />
		</intent>

		<!-- Camera -->
		<intent>
		    <action android:name="android.media.action.IMAGE_CAPTURE" />
		</intent>

		<!-- Gallery -->
		<intent>
		    <action android:name="android.intent.action.GET_CONTENT" />
		</intent>
		<!-- Gallery -->
		<intent>
		    <action android:name="android.intent.action.PICK" />
		</intent>
    	</queries>
</manifest>
```

res/xml/file_paths.xml
```
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="my_images"
        path="Android/data/com.tuanfadbg.takephotoutilsdemo/files/Pictures" />
</paths>
```

```java
    TakePhotoUtils takePhotoUtils;
    
    takePhotoUtils = new TakePhotoUtils(this, "com.tuanfadbg.takephotoutilsdemo.fileprovider");

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
```

```java
// dialog with 2 options take photo and gallery
	    takePhotoUtils.showDialogSelectImage(null, null, null)
                .setListener(new TakePhotoCallback() {
                    @Override
                    public void onSuccess(String path, int width, int height) {

                    }

                    @Override
                    public void onFail() {

                    }
                });

        // take photo
        takePhotoUtils.takePhoto()
                .setListener(new TakePhotoCallback() {
                    @Override
                    public void onSuccess(String path, int width, int height) {

                    }

                    @Override
                    public void onFail() {

                    }
                });

        // gallery
        takePhotoUtils.getImageFromGallery()
                .setListener(new TakePhotoCallback() {
                    @Override
                    public void onSuccess(String path, int width, int height) {

                    }

                    @Override
                    public void onFail() {

                    }
                });
```

## More Options
`.toPortrait()`

`.resize(int width, int height)`

`.resizeToMaxSide(int side)`

`.quality(int quality)`


## Author

 *Tuan Nguyen* - Tuan FADBG.

## Versioning
We use Jitpack for versioning. For the versions available, see the tags on this repository.
