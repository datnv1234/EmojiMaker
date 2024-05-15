package com.wa.ai.emojimaker.ui.dialog

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.BitmapSticker
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.utils.AppUtils.convertFileToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AddToPackageViewModel : BaseViewModel() {
    private val _packageMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val packageMutableLiveData: LiveData<List<String>>
        get() = _packageMutableLiveData

    fun getPackage(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listEntry = mutableListOf<String>()
            val cw = ContextWrapper(context)
            val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        listEntry.add(file.toString())
                    }
                }
            }
            _packageMutableLiveData.postValue(listEntry)
        }
    }
}