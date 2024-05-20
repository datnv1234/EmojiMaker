package com.wa.ai.emojimaker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static File copyRawFileToInternalStorage(Context context, int rawResId, String fileName) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        File file = new File(context.getFilesDir(), fileName);

        try {
            inputStream = context.getResources().openRawResource(rawResId);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }

    public static void shareFile(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/octet-stream"); // Đặt loại tệp tùy theo tệp của bạn
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(intent, "Share file"));
    }

    public static File copyFileToInternalStorage(Context context, File sticker, String fileName) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        File file = new File(context.getFilesDir(), fileName);

        try {
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}