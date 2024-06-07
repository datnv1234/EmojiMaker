package com.wa.ai.emojimaker.ui.component.main

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {

    var category: String? = null
    var categorySize: Int = 0

    private val listCategory = listOf(
        "brainy_endeavors",
        "cat_chic",
        "couple_emoji",
        "cute_girl",
        "dog_diversity",
        "emoji",
        "funny_cat",
        "funny_rat",
        "orange_orchard",
        "pet_pawtentials",
        "quacking_quacks",
        "sly_spirits",
        "xiximi")

    private val _stickerMutableLiveData: MutableLiveData<List<MadeStickerModel>> = MutableLiveData()
    val stickerMutableLiveData: LiveData<List<MadeStickerModel>>
        get() = _stickerMutableLiveData

    fun getStickers(context: Context) {
        category = listCategory.random()
        val assetManager = context.assets
        val listEntry = mutableListOf<MadeStickerModel>()
        viewModelScope.launch(Dispatchers.IO) {
            val listFile = assetManager.list("categories/$category")

            if (listFile != null) {
                for (file in listFile) {
                    val inputStream = assetManager.open("categories/$category/$file")
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    listEntry.add(MadeStickerModel(category, null, bitmap))
                    _stickerMutableLiveData.postValue(listEntry)
                    inputStream.close()
                }
            }
            categorySize = listEntry.size
            _stickerMutableLiveData.postValue(listEntry)
        }
    }

}