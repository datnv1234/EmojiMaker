package com.wa.ai.emojimaker.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcelable
import android.widget.Toast
import androidx.core.content.FileProvider
import com.wa.ai.emojimaker.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.jvm.internal.Intrinsics

object AppUtils {

    fun shareImage(context: Context, bitmap: Bitmap) {
        try {
            // Save the image to a temporary file
            val fileName = "temp_image.png"
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, fileName)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            // Create the share intent
            val imageUri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("image/png")
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)

            context.startActivity(Intent.createChooser(shareIntent, "Share Sticker"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "Failed to share image",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun importToTelegram(context: Context, list: List<Uri>) {
        Intrinsics.checkNotNullParameter(context, "context")
        Intrinsics.checkNotNullParameter(list, "uriList")
        val arrayList: ArrayList<Parcelable> = java.util.ArrayList<Parcelable>(list)
        val it: Iterator<*> = arrayList.iterator()
        Intrinsics.checkNotNullExpressionValue(it, "list.iterator()")
        while (it.hasNext()) {
            context.grantUriPermission("org.telegram.messenger", it.next() as Uri?, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val intent = Intent("org.telegram.messenger.CREATE_STICKER_PACK")
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList)
        intent.putExtra("IMPORTER", context.packageName)
        //intent.setFlags(268435457)
//        268435457
        intent.setType("image/*")
        try {
            context.startActivity(intent)
        } catch (unused: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.no_app_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun shareMultipleImages(context: Context, list: List<Uri>) {
        Intrinsics.checkNotNullParameter(context, "context")
        Intrinsics.checkNotNullParameter(list, "uriList")
        val intent = Intent()
        intent.setAction("android.intent.action.SEND_MULTIPLE")
        intent.setType("image/*")
        intent.putParcelableArrayListExtra(
            "android.intent.extra.STREAM",
            java.util.ArrayList<Parcelable>(list)
        )
        try {
            val createChooser = Intent.createChooser(intent, "Share images to..")
            Intrinsics.checkNotNullExpressionValue(
                createChooser,
                "createChooser(shareIntent, \"Share images to..\")"
            )
            val queryIntentActivities =
                context.packageManager.queryIntentActivities(createChooser, 65536)
            Intrinsics.checkNotNullExpressionValue(
                queryIntentActivities,
                "context.packageManager\n …nager.MATCH_DEFAULT_ONLY)"
            )
            for (resolveInfo in queryIntentActivities) {
                val str = resolveInfo.activityInfo.packageName
                for (grantUriPermission in list) {
                    context.grantUriPermission(str, grantUriPermission, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
            context.startActivity(createChooser)
        } catch (unused: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.no_app_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun convertFileToBitmap(file: File): Bitmap {
        lateinit var bitmap: Bitmap
        try {
            // Đọc tệp vào một đối tượng FileInputStream
            val fis = FileInputStream(file)

            // Đọc dữ liệu từ FileInputStream và chuyển đổi thành Bitmap bằng BitmapFactory
            bitmap = BitmapFactory.decodeStream(fis)

            // Đóng FileInputStream
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

}