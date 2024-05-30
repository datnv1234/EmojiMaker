package com.wa.ai.emojimaker.ui.component.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.service.RemoteConfigService
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val remoteConfigService: RemoteConfigService
) : BaseViewModel() {

    fun fetchTokenRemoteConfig(): Boolean {
        return remoteConfigService.fetchAndSaveForceUpdate()
    }

    private val _typeNextScreen: MutableLiveData<Int> = MutableLiveData()
    val typeNextScreen: LiveData<Int>
        get() = _typeNextScreen


    fun getTypeNextScreen() = viewModelScope.launch(Dispatchers.IO) {
        _typeNextScreen.postValue(
            SharedPreferenceHelper.getInt(
                Constant.KEY_FIRST_SHOW_INTRO,
                Constant.TYPE_SHOW_LANGUAGE_ACT
            )
        )
    }

}