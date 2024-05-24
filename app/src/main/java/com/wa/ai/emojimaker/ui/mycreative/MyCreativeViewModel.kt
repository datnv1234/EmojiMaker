package com.wa.ai.emojimaker.ui.mycreative

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.utils.AppUtils.convertFileToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MyCreativeViewModel : BaseViewModel() {
    private val _stickerMutableLiveData: MutableLiveData<List<PackageModel>> = MutableLiveData()
    val stickerMutableLiveData: LiveData<List<PackageModel>>
        get() = _stickerMutableLiveData

    fun getItemSticker(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listEntry = mutableListOf<PackageModel>()
            val cw = ContextWrapper(context)
            val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
            val files = directory.listFiles()      // Get packages
            if (files != null) {                    //package's size > 0
                for (file in files) {
                    if (file.isDirectory) {
                        var bitmap : Bitmap? = null
                        val item = file.listFiles()
                        if (item != null) {
                            if (item.isNotEmpty()) {
                                bitmap = convertFileToBitmap(item.random())
                            }
                        }
                        val name = file.name.replace("_", " ")
                        listEntry.add(PackageModel(name, item?.size ?: 0, bitmap))
                    }
                }
                _stickerMutableLiveData.postValue(listEntry)
            }
        }
    }



}