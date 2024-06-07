package com.wa.ai.emojimaker.ui.component.showstickers

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ShowStickerViewModel : BaseViewModel() {

    var stickerUri = ArrayList<Uri>()
    var stickerBitmaps = ArrayList<Bitmap>()

    private val _stickersMutableLiveData: MutableLiveData<List<MadeStickerModel>> = MutableLiveData()
    private val _localStickerMutableLiveData: MutableLiveData<List<MadeStickerModel>> = MutableLiveData()

    val stickersMutableLiveData: LiveData<List<MadeStickerModel>>
        get() = _stickersMutableLiveData

    val localStickerMutableLiveData: LiveData<List<MadeStickerModel>>
        get() = _localStickerMutableLiveData

    fun getLocalSticker(context: Context, category: String, size: Int) {

        val listSticker = mutableListOf<MadeStickerModel>()
        for (i in 0 until size) {
            listSticker.add(MadeStickerModel("", "", null))
        }
        val listEntry = mutableListOf<MadeStickerModel>()
        listEntry.addAll(listSticker)
        viewModelScope.launch(Dispatchers.IO) {

            val cw = ContextWrapper(context)
            val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
            val files = directory.listFiles()      // Get packages
            if (files != null) {                    //package's size > 0
                for (file in files) {
                    if (file.isDirectory && file.name.equals(category)) {
                        val stickers = file.listFiles()
                        if (stickers != null) {
                            for ((i, sticker) in stickers.withIndex()) {
                                val name : String = sticker.name
                                val bitmap : Bitmap = AppUtils.convertFileToBitmap(sticker)
                                listEntry[i].name = name
                                listEntry[i].packageName = category
                                listEntry[i].bitmap = bitmap

                                _localStickerMutableLiveData.postValue(listEntry)
                            }
                        }
                        break
                    }
                }
            }
            _localStickerMutableLiveData.postValue(listEntry)
        }
    }

    fun getStickers(context: Context, category: String, size: Int) {
        val assetManager = context.assets
        val listEntry = mutableListOf<MadeStickerModel>()
        viewModelScope.launch(Dispatchers.IO) {
            val listFile = assetManager.list("categories/$category")

            if (listFile != null) {
                for ((i, file) in listFile.withIndex()) {
                    val inputStream = assetManager.open("categories/$category/$file")
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    listEntry.add(MadeStickerModel(category, null, bitmap))
                    _stickersMutableLiveData.postValue(listEntry)
                    inputStream.close()
                }
            }
            _stickersMutableLiveData.postValue(listEntry)
        }
//        val countDownTimer: CountDownTimer = object : CountDownTimer(5000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//            }
//
//            override fun onFinish() {
//                _stickersMutableLiveData.postValue(listEntry)
//            }
//        }
//        countDownTimer.start()
    }
}