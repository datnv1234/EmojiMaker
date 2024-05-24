package com.wa.ai.emojimaker.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.jvm.internal.Intrinsics

object AppUtils {

    private val CREATE_STICKER_PACK_ACTION = "org.telegram.messenger.CREATE_STICKER_PACK"
    private val CREATE_STICKER_PACK_EMOJIS_EXTRA = "STICKER_EMOJIS"
    private val CREATE_STICKER_PACK_IMPORTER_EXTRA = "IMPORTER"
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

    fun Bitmap.scaleWith(scale: Float) = Bitmap.createScaledBitmap(
        this,
        (width * scale).toInt(),
        (height * scale).toInt(),
        false
    )
    fun doImport(context: Context, stickerUris: ArrayList<Uri>) {
        val it = stickerUris.iterator()
        while (it.hasNext()) {
            context.grantUriPermission(
                "org.telegram.messenger",
                it.next(),
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        val intent = Intent(CREATE_STICKER_PACK_ACTION)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, stickerUris)
        intent.putExtra("IMPORTER", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.type = "image/*"

        try {
            val shareIntent = Intent.createChooser(intent, null)
            context.startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.app_name), Toast.LENGTH_SHORT).show()
        }
    }

    fun doImportWhatsApp(context: Context, stickerUris: ArrayList<Uri>) {
        val it = stickerUris.iterator()
        while (it.hasNext()) {
            context.grantUriPermission(
                "com.whatsapp",
                it.next(),
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        val intent = Intent("com.whatsapp.intent.action.ENABLE_STICKER_PACK")
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, stickerUris)
        intent.putExtra("IMPORTER", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.type = "image/webp"

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.app_name), Toast.LENGTH_SHORT).show()
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

    fun saveSticker(context: Context, finalBitmap: Bitmap, category: String) {
        val root = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString()
        val myDir = File("$root/AIEmojiMaker/")
        myDir.mkdirs()
        val fname : String = System.currentTimeMillis().toString() + ".png"
        val file = File(myDir, fname)
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()), null
        ) { path, uri ->
            Timber.tag("ExternalStorage").i("Scanned: %s", path)
            Timber.tag("ExternalStorage").i("-> uri= %s", uri)
        }
    }

    fun checkPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED)
    }

    fun requestPermissionAndContinue(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    Constant.PERMISSION_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), Constant.PERMISSION_REQUEST_CODE
                )
            }
        } else {

        }
    }

}