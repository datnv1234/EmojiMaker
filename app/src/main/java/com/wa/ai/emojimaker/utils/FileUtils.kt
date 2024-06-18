package com.wa.ai.emojimaker.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtils {
    fun copyFileToCache(context: Context, sourceFile: File): File {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        val file = File(context.filesDir, sourceFile.name)
        try {
            inputStream = FileInputStream(sourceFile)
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun copyAssetFileToCache(context: Context, sourceFile: InputStream, fileName: String?): File {
        var outputStream: FileOutputStream? = null
        val file = File(context.filesDir, fileName)
        try {
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            while (sourceFile.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
//                sourceFile.close();
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun getUriForFile(context: Context, file: File?): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file!!)
    }

    fun shareFile(context: Context, file: File?) {
        val fileUri =
            FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file!!)
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("application/octet-stream") // Đặt loại tệp tùy theo tệp của bạn
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Share file"))
    }

    fun copyFileToInternalStorage(context: Context, sticker: File?, fileName: String?): File {
        val inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        val file = File(context.filesDir, fileName)
        try {
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream!!.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun getBitmapFromAssets(context: Context, fileName: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream = context.assets.open(fileName)
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

}