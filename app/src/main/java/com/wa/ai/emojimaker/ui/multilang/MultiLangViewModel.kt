package com.wa.ai.emojimaker.ui.multilang

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.data.model.LanguageUI
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.ui.base.toMutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MultiLangViewModel : BaseViewModel() {
    private val _languageLiveData: MutableLiveData<List<LanguageUI>> = MutableLiveData()
    private fun getValueLanguage() = _languageLiveData.toMutableList { it.copy() }
    val languageLiveData: LiveData<List<LanguageUI>>
        get() = _languageLiveData

    fun insertLanguage(languageUI: LanguageUI) = viewModelScope.launch(Dispatchers.IO) {
        getValueLanguage().let { temp ->
            temp.add(languageUI)
            _languageLiveData.postValue(temp)
        }
    }


    fun getListLanguage() = viewModelScope.launch(Dispatchers.IO) {
        val listLanguageDefault = mutableListOf<LanguageUI>()
        val language1 = LanguageUI(
            name = "English",
            code = "en",
            avatar = R.drawable.img_english,
        )
        val language2 = LanguageUI(
            name = "Spanish", code = "es", avatar = R.drawable.img_spanish,
        )
        val language3 = LanguageUI(
            name = "French", code = "fr", avatar = R.drawable.img_french,
        )
        val language4 = LanguageUI(
            name = "Hindi",
            code = "hi",
            avatar = R.drawable.img_india,
        )
        val language5 = LanguageUI(
            name = "Turkish", code = "pt", avatar = R.drawable.img_portuguese,
        )
        val language6 = LanguageUI(
            name = "Vietnamese", code = "vi", avatar = R.drawable.img_vietnam,
        )
        listLanguageDefault.add(language1)
        listLanguageDefault.add(language2)
        listLanguageDefault.add(language3)
        listLanguageDefault.add(language4)
        listLanguageDefault.add(language5)
        listLanguageDefault.add(language6)
        _languageLiveData.postValue(listLanguageDefault)
    }

    fun saveFirstKeyIntro() =
        viewModelScope.launch(Dispatchers.IO) {
            SharedPreferenceHelper.storeInt(
                Constant.KEY_FIRST_SHOW_INTRO,
                Constant.TYPE_SHOW_INTRO_ACT
            )
        }

}