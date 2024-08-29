package com.wa.ai.emojimaker.ui.component.emojimerge

import android.app.Activity
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.data.model.EmojiUI
import com.wa.ai.emojimaker.data.repo.emoji.EmojiRepo
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MergeVM @Inject constructor(private val emojiRepo: EmojiRepo) : BaseViewModel() {

    private val _loadBanner: MutableLiveData<Boolean> = MutableLiveData()
    val loadBanner: LiveData<Boolean>
        get() = _loadBanner

    private var timerReloadBanner : CountDownTimer? = null

    private val _emojiMutableLiveData: MutableLiveData<List<EmojiUI>> = MutableLiveData()
    val emojiLiveData: LiveData<List<EmojiUI>>
        get() = _emojiMutableLiveData

    fun getAllListEmoji(activity: Activity) = viewModelScope.launch(Dispatchers.IO) {
        emojiRepo.getAllEmoji(activity).flowOn(Dispatchers.IO).collect {
             kotlin.runCatching {
                 _emojiMutableLiveData.postValue(it)
             }.onFailure {
                 it.printStackTrace()
             }
        }
    }

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
}