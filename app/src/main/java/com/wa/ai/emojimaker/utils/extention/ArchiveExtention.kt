package com.wa.ai.emojimaker.utils.extention

import android.content.Context
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun Context.unzipFromAssets(assetFilePath: String, outputFolder: String): List<String> {
    val listPath: MutableList<String> = mutableListOf()
    try {
        val outputFolderFile = File(outputFolder).mkdirs()
        val inputStream: InputStream = assets.open(assetFilePath)
        val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))
        var zipEntry: ZipEntry? = zipInputStream.nextEntry
        while (zipEntry != null) {
            val outputFile = File(outputFolder, zipEntry.name)
            listPath.add(outputFile.absolutePath)
            if (zipEntry.isDirectory) {
                outputFile.mkdirs()
            } else {
                val outputStream = FileOutputStream(outputFile)
                val bufferedOutputStream = BufferedOutputStream(outputStream)

                val buffer = ByteArray(1024)
                var len: Int
                while (zipInputStream.read(buffer).also { len = it } > 0) {
                    bufferedOutputStream.write(buffer, 0, len)
                }

                bufferedOutputStream.close()
                outputStream.close()
            }

            zipEntry = zipInputStream.nextEntry
        }

        zipInputStream.closeEntry()
        zipInputStream.close()
    } catch (e: IOException) {
        Timber.e("datnv: $e")
    }
    return listPath
}
