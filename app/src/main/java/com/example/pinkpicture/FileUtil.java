package com.example.pinkpicture;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class FileUtil {

    public static String getPathFromUri(final Context context, final Uri uri) {
        File file = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean success = false;
        try {
            String extension = getImageExtension(context, uri);
            inputStream = context.getContentResolver().openInputStream(uri);
            File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!Objects.equals(Environment.getExternalStorageState(documentsDir), Environment.MEDIA_MOUNTED)) {
                documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            }
            if (!Objects.equals(Environment.getExternalStorageState(documentsDir), Environment.MEDIA_MOUNTED)) {
                documentsDir = new File(context.getFilesDir(), Environment.DIRECTORY_DOCUMENTS);
            }
            file = File.createTempFile("file", extension, documentsDir);
            file.deleteOnExit();
            outputStream = new FileOutputStream(file);
            if (inputStream != null) {
                copy(inputStream, outputStream);
                success = true;
            }
        } catch (IOException ignored) {
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException ignored) {
            }
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException ignored) {
                success = false;
            }
        }
        return success ? file.getPath() : null;
    }

    public static String getImageExtension(Context context, Uri uriImage) {
        String extension = null;
        try {
            if (uriImage.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uriImage));
            } else {
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uriImage.getPath())).toString());
            }
        } catch (Exception e) {
            extension = null;
        }
        if (extension == null || extension.isEmpty()) {
            extension = "jpg";
        }
        return "." + extension;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[4 * 1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
    }
}
