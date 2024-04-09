package com.wa.ai.emojimaker.utils.extention

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.Inet6Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.Calendar
import kotlin.random.Random

fun Long.getDateTime(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return calendar.get(Calendar.DATE).toString()
        .padStart(2, '0') + "/" + (calendar.get(Calendar.MONTH) + 1).toString()
        .padStart(2, '0') + "/" + calendar.get(
        Calendar.YEAR
    ).toString().padStart(2, '0')
}

fun Any.getLog(tag: String? = "", msg: String? = "Invoked") =
    "${this.javaClass.simpleName}#${this.hashCode()}.$tag@${Thread.currentThread().name}: $msg"

fun InetAddress.asString(): String = if (this is Inet6Address) "[${this.hostAddress}]" else this.hostAddress ?: ""

fun InetSocketAddress?.asString(): String = "${this?.hostName?.let { it + "\n" }}${this?.address?.asString()}:${this?.port}"


internal fun randomPin(): String = Random.nextInt(10).toString() + Random.nextInt(10).toString() +
        Random.nextInt(10).toString() + Random.nextInt(10).toString() +
        Random.nextInt(10).toString() + Random.nextInt(10).toString()

internal fun randomString(len: Int): String {
    val chars = CharArray(len)
    val symbols = "0123456789abcdefghijklmnopqrstuvwxyz"
    for (i in 0 until len) chars[i] = symbols.random()
    return String(chars)
}

internal fun Context.getFileFromAssets(fileName: String): ByteArray {
    assets.open(fileName).use { inputStream ->
        val fileBytes = ByteArray(inputStream.available())
        inputStream.read(fileBytes)
        fileBytes.isNotEmpty() || throw IllegalStateException("$fileName is empty")
        return fileBytes
    }
}


fun copyToClipboard(text: CharSequence, activity: Activity) {
    val clipboard = activity.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(
        activity,"Link copied",
        Toast.LENGTH_SHORT
    ).show()
}

fun String.convertMiniToMinutesSeconds(miniTime: Int) {
    val totalSeconds = miniTime * 60 / 100
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
}
fun String.removeSpecialCharacters(): String {
    // Sử dụng biểu thức chính quy để loại bỏ các ký tự đặc biệt
    return this.replace(Regex("[^a-zA-Z0-9\\s]+"), "@")
}

fun String.containsNonAllowedCharacters(): Boolean {
    return this.contains(Regex("[^a-zA-Z0-9\\sàáảãạăắằẳẵặâấầẩẫậđèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵ]+"))
}