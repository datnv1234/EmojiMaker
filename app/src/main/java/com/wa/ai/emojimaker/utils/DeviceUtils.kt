package com.wa.ai.emojimaker.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.coroutineContext


object DeviceUtils {

    /**
     * Kiểm tra sự có sẵn của quyền root
     */
    fun isRootAvailable(): Boolean {
        System.getenv("PATH")?.split(":")?.forEach { pathDir ->
            if (java.io.File(pathDir, "su").exists()) {
                return true
            }
        }
        return false
    }

    /**
     * Kiểm tra trạng thái kết nối Wifi
     */
    fun checkWifiConnected(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (mWifi != null) {
            return mWifi.isConnected
        }
        return false
    }

    /**
     * Kiểm tra loại kết nối của thiết bị
     *
     * @return 0: Không kết nối, 1: Wifi, 2: Mobile
     */
    fun getConnectType(context: Context): Int {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.isConnectedOrConnecting == true) {
            return 1
        }

        if (connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.isConnectedOrConnecting == true) {
            return 2
        }

        return 0
    }

    /**
     * Kiểm tra trạng thái kết nối Internet
     */
    fun checkInternetConnection(ctx: Context): Boolean {
        val conMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return try {
            (conMgr.activeNetworkInfo?.isAvailable == true
                    && conMgr.activeNetworkInfo?.isConnected == true)
        } catch (e: NullPointerException) {
            false
        }
    }
    fun Context.openPlayStoreForRating() {
        val uri = Uri.parse("market://details?id=$packageName")
        val rateIntent = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            // Nếu không tìm thấy Google Play Store, hiển thị thông báo hoặc hướng dẫn cài đặt
            Toast.makeText(this, "Không thể mở Google Play Store", Toast.LENGTH_SHORT).show()
        }
    }

    fun getPublicDirectoryPath(directoryType: String): String? {
        return Environment.getExternalStoragePublicDirectory(directoryType)?.absolutePath
    }

    fun savePNGToInternalStorage(
        context: Context,
        folder: String,
        bitmapImage: Bitmap,
        fileName: String = "${System.currentTimeMillis()}.png"
    ): String {
        val cw = ContextWrapper(context)
        // Path to /data/data/your_app/app_data/imageDir
        val directory: File = cw.getDir(folder, Context.MODE_PRIVATE)
        // Tạo tệp lưu trữ ảnh
        val path = File(directory, fileName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(path)
            // Lưu ảnh vào FileOutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }
}
