package com.wa.ai.emojimaker.ui.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.data.model.StickerUri
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    var stickerUri = mutableListOf<Uri>()

    private val _categoriesMutableLiveData: MutableLiveData<List<Category>> = MutableLiveData()

    val categoriesMutableLiveData: LiveData<List<Category>>
        get() = _categoriesMutableLiveData

    fun getCategoryList() {
        viewModelScope.launch(Dispatchers.IO) {
            val listEntry = mutableListOf<Category>()
            listEntry.add(Category("cat_chic", "Cat Chic"))
            listEntry.add(Category("orange_orchard", "Orange Orchard"))
            listEntry.add(Category("funny_cat", "Funny rat"))
            listEntry.add(Category("dog_diversity", "Dog Diversity"))
            listEntry.add(Category("pet_pawtentials", "Pet Pawtentials"))
            listEntry.add(Category("sly_spirits", "Sly Spirits"))
            listEntry.add(Category("xiximi", "Xiximi"))
            listEntry.add(Category("funny_cat", "Funny Cat"))
            listEntry.add(Category("quacking_quacks", "Quacking Quacks"))
            listEntry.add(Category("emoji", "Emoji"))
            listEntry.add(Category("brainy_endeavors", "Brainy Endeavors"))
            listEntry.add(Category("cute_baby", "Cute Baby"))
            listEntry.add(Category("folk_emoji", "Folk Emoji"))
            listEntry.add(Category("panda_pals", "Panda Pals"))
            listEntry.add(Category("couple_emoji", "Couple emoji"))
            listEntry.add(Category("cute_girl", "Cute girl"))

            _categoriesMutableLiveData.postValue(listEntry)
            //Log.d(TAG, "getCategoryList: " + _categoriesMutableLiveData.value?.size)
            //Log.d(TAG, "getCategoryList: " + listEntry.size)
        }
    }
}