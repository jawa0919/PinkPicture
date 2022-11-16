package com.example.pinkpicture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int requestCode_take = 10001;
    private static final int requestCode_choose = 10002;
    private File storage = null;
    private String authority = null;
    private File cameraFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        List<File> dirs = new ArrayList<File>();
        dirs.add(getCacheDir());
        dirs.add(getFilesDir());
        dirs.add(getExternalCacheDir());
        dirs.add(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        dirs.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        for (File dir : dirs) {
            Log.d(TAG, "dir: " + dir.getAbsolutePath());
        }
        storage = Environment.getExternalStorageDirectory();
        authority = getPackageName() + ".FileProvider";
        Log.d(TAG, "storage.State: " + Environment.getExternalStorageState());
        Log.d(TAG, "storage.AbsolutePath: " + storage.getAbsolutePath());
        findViewById(R.id.take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "takePicture", Toast.LENGTH_SHORT).show();
                try {
                    takePicture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "choosePicture", Toast.LENGTH_SHORT).show();
                choosePicture();
            }
        });
    }

    /**
     * 拍摄照片保存在外部缓存文件夹下
     * 压缩照片保存在公共图片件夹下
     */
    private void takePicture() throws IOException {
        cameraFile = File.createTempFile("take", ".jpg", getExternalCacheDir());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(this, authority, cameraFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, requestCode_take);
    }

    private void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, requestCode_choose);
    }

    private void saveToAlbum(String path) {
        File imageFile = new File(path);
        if (!imageFile.exists()) return;
        Uri uri = FileProvider.getUriForFile(this, authority, imageFile);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == requestCode_take) {
            Log.d(TAG, "requestCode_take: " + cameraFile.getAbsolutePath());
            String path = CompressImage.compressImage(cameraFile.getAbsolutePath());
//            saveToAlbum(path);
            Toast.makeText(MainActivity.this, "takePicture: " + path, Toast.LENGTH_SHORT).show();
        }
        if (resultCode == RESULT_OK && requestCode == requestCode_choose && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String path = FileUtil.getPathFromUri(this, uri);
            Log.d(TAG, "requestCode_choose: " + path);
            Toast.makeText(MainActivity.this, "choosePicture: " + path, Toast.LENGTH_SHORT).show();
        }
    }
}