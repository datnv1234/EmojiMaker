package com.wa.ai.emojimaker.ui.showstickers

import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.PieceSticker
import com.wa.ai.emojimaker.data.model.StickerUri
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowStickerViewModel : BaseViewModel() {

    private val _stickersMutableLiveData: MutableLiveData<List<StickerUri>> = MutableLiveData()

    val stickersMutableLiveData: LiveData<List<StickerUri>>
        get() = _stickersMutableLiveData

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