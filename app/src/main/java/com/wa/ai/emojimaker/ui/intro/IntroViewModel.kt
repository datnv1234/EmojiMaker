package com.wa.ai.emojimaker.ui.intro

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.IntroUI
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IntroViewModel : BaseViewModel() {
    private val _introMutableLiveData: MutableLiveData<List<IntroUI>> = MutableLiveData()
    val introMutableLiveData: LiveData<List<IntroUI>>
        get() = _introMutableLiveData

    fun getIntro(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listIntroEntity = mutableListOf<IntroUI>()
            val titlesResourceIds = listOf(
                R.string.content_intro_1,
                R.string.content_intro_2,
                R.string.content_intro_3,

                )

            val contentsResourceIds = listOf(
                R.string.content_intro_1,
                R.string.content_intro_2,
                R.string.content_intro_3,
            )

            val drawableResourceIds = listOf(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background,
            )

            for (i in titlesResourceIds.indices) {
                listIntroEntity.add(
                    IntroUI(
                        context.getString(titlesResourceIds[i]),
                        "",
                        drawableResourceIds[i]
                    )
                )
            }
            _introMutableLiveData.postValue(listIntroEntity)
        }
    }
}