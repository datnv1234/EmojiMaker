package com.wa.ai.emojimaker.ui.showstickers

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.data.model.PieceSticker
import com.wa.ai.emojimaker.data.model.StickerUri
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ShowStickerViewModel : BaseViewModel() {

    private val _stickersMutableLiveData: MutableLiveData<List<StickerUri>> = MutableLiveData()
    private val _localStickerMutableLiveData: MutableLiveData<List<MadeStickerModel>> = MutableLiveData()

    val stickersMutableLiveData: LiveData<List<StickerUri>>
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
                            var i = 0
                            for (sticker in stickers) {
                                val name : String = sticker.name
                                val bitmap : Bitmap = AppUtils.convertFileToBitmap(sticker)
                                listEntry[i].name = name
                                listEntry[i].packageName = category
                                listEntry[i].bitmap = bitmap
                                i++

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

    fun getStickers(category: String, size: Int) {
        val listStickerUri = mutableListOf<StickerUri>()
        for (i in 0 until size) {
            listStickerUri.add(StickerUri("".toUri()))
        }
        val listEntry = mutableListOf<StickerUri>()
        viewModelScope.launch(Dispatchers.IO) {
            listEntry.addAll(listStickerUri)
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child(category)
            storageRef.listAll().addOnSuccessListener { listResult ->
                var i = 0
                for (item in listResult.items) {
                    item.downloadUrl.addOnSuccessListener { uri ->
                        listEntry[i].uri = uri
                        i++
                        _stickersMutableLiveData.postValue(listEntry)
                    }
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