package com.example.pinkpicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class CompressImage {

    public static String compressImage(final Context context, final String originalPath) {
        File originalFile = new File(originalPath);
        if (!originalFile.exists()) return "";
        Bitmap bitmap = compressBitmap(BitmapFactory.decodeFile(originalPath));
        return saveBitmap(context, bitmap);
    }

    public static Bitmap compressBitmap(final Bitmap image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        int options = 90;
        while (os.toByteArray().length > 500 * 1024) {
            os.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
            options -= 10;
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        return BitmapFactory.decodeStream(is, null, null);
    }

    public static String saveBitmap(Context context, Bitmap image) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!Objects.equals(Environment.getExternalStorageState(dir), Environment.MEDIA_MOUNTED)) {
                dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            }
            if (!Objects.equals(Environment.getExternalStorageState(dir), Environment.MEDIA_MOUNTED)) {
                dir = new File(context.getFilesDir(), Environment.DIRECTORY_DOCUMENTS);
            }
            File filePic = File.createTempFile("compress", ".jpg", dir);
            FileOutputStream fos = new FileOutputStream(filePic);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return filePic.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
