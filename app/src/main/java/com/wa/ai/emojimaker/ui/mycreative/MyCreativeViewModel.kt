package com.wa.ai.emojimaker.ui.mycreative

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.BitmapSticker
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class MyCreativeViewModel : BaseViewModel() {
    private val _stickerMutableLiveData: MutableLiveData<List<BitmapSticker>> = MutableLiveData()
    val stickerMutableLiveData: LiveData<List<BitmapSticker>>
        get() = _stickerMutableLiveData

    fun getItemSticker(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listEntry = mutableListOf<BitmapSticker>()
            val cw = ContextWrapper(context)
            val directory: File = cw.getDir("mySticker", Context.MODE_PRIVATE)
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    val bitmap = convertFileToBitmap(file)
                    listEntry.add(BitmapSticker(bitmap))
                }
            }
            Log.d(Constant.TAG, "getItemSticker: ${listEntry.size}")
            _stickerMutableLiveData.postValue(listEntry)
        }
    }


    private fun convertFileToBitmap(file: File): Bitmap {
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