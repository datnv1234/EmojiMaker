package com.wa.ai.emojimaker.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
}