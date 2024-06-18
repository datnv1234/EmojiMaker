package com.wa.ai.emojimaker.ui.component.showstickers

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.data.model.Sticker
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class ShowStickerViewModel : BaseViewModel() {

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        timerReloadBanner?.cancel()
        stickerUri.clear()
    }

    var stickerUri = ArrayList<Uri>()
    private var timerReloadBanner: CountDownTimer? = null

    private val _stickersMutableLiveData: MutableLiveData<List<MadeStickerModel>> =
        MutableLiveData()
    private val _localStickerMutableLiveData: MutableLiveData<List<MadeStickerModel>> =
        MutableLiveData()

    val stickersMutableLiveData: LiveData<List<MadeStickerModel>>
        get() = _stickersMutableLiveData

    val localStickerMutableLiveData: LiveData<List<MadeStickerModel>>
        get() = _localStickerMutableLiveData

    private val _loadBanner: MutableLiveData<Boolean> = MutableLiveData()
    val loadBanner: LiveData<Boolean>
        get() = _loadBanner

    private fun createCountDownTimerReloadBanner(time: Long): CountDownTimer {
        return object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                _loadBanner.postValue(true)
            }
        }
    }


    fun starTimeCountReloadBanner(time: Long) {
        kotlin.runCatching {
            timerReloadBanner?.cancel()
            timerReloadBanner = createCountDownTimerReloadBanner(time)
            timerReloadBanner?.start()
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun getCreativeSticker(context: Context, category: String) {
        val listEntry = mutableListOf<MadeStickerModel>()
        viewModelScope.launch(Dispatchers.IO) {
            val cw = ContextWrapper(context)
            val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
            val files = directory.listFiles()      // Get packages
            if (files != null) {                    //package's size > 0
                for (file in files) {
                    if (file.isDirectory && file.name.equals(category)) {
                        file.listFiles()?.forEach {
                            listEntry.add(
                                MadeStickerModel(
                                    it.name, category, path = it.path
                                )
                            )
                        }
                        break
                    }
                }
            }
            _localStickerMutableLiveData.postValue(listEntry)
        }
    }

    fun getStickers(context: Context, category: String) {
        val assetManager = context.assets
        val listEntry = mutableListOf<MadeStickerModel>()
        viewModelScope.launch(Dispatchers.IO) {
            assetManager.list("categories/$category")?.forEach {
                listEntry.add(
                    MadeStickerModel(
                        it, category, path = "file:///android_asset/categories/$category/$it"
                    )
                )
            }
            _stickersMutableLiveData.postValue(listEntry)
        }
    }
}