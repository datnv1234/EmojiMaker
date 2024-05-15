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
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.BitmapSticker
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.utils.AppUtils.convertFileToBitmap
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
            val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        var bitmap : Bitmap? = null
                        val item = file.listFiles()
                        if (item != null) {
                            if (item.isNotEmpty()) {
                                bitmap = convertFileToBitmap(item.random())
                            }
                        }
                        listEntry.add(BitmapSticker(bitmap))
                    }
                }
            }
            _stickerMutableLiveData.postValue(listEntry)
        }
    }



}