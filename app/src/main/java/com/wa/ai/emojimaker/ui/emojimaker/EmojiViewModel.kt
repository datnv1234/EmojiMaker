package com.wa.ai.emojimaker.ui.emojimaker

import android.content.Context
import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PagerIconUI
import com.wa.ai.emojimaker.data.model.PieceSticker
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EmojiViewModel : BaseViewModel() {
    private val _optionMutableLiveData: MutableLiveData<List<PagerIconUI>> = MutableLiveData()
    val optionMutableLiveData: LiveData<List<PagerIconUI>>
        get() = _optionMutableLiveData

    private val _stickerMutableLiveData: MutableLiveData<List<List<PieceSticker>>> = MutableLiveData()
    val stickerMutableLiveData: LiveData<List<List<PieceSticker>>>
        get() = _stickerMutableLiveData

    fun getItemOption(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listOptionEntity = mutableListOf<PagerIconUI>()
            val titles = listOf(
                R.string.face,
                R.string.accessories,
                R.string.beard,
                R.string.brow,
                R.string.eyes,
                R.string.glass,
                R.string.hair,
                R.string.hand,
                R.string.hat,
                R.string.mouth,
                R.string.nose)

            val folders = listOf(
                "face",
                "accessories",
                "beard",
                "brow",
                "eyes",
                "glass",
                "hair",
                "hand",
                "hat",
                "mouth",
                "nose",
            )

            for (i in titles.indices) {
                listOptionEntity.add(
                    PagerIconUI(
                        context.getString(titles[i]),
                        folders[i],
                    )
                )
            }
            _optionMutableLiveData.postValue(listOptionEntity)
        }
    }

    fun getPieceSticker(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val assetManager = context.assets
            val access = assetManager.list("accessories/")
            if (access != null) {
                for (file in access) {

                }
            }
            val listAccessories = mutableListOf<PieceSticker>()
            val drawable = listOf(
                R.string.face,
                R.string.accessories,
                R.string.beard,
                R.string.brow,
                R.string.eyes,
                R.string.glass,
                R.string.hair,
                R.string.hand,
                R.string.hat,
                R.string.mouth,
                R.string.nose)

            val folders = listOf(
                "face",
                "accessories",
                "beard",
                "brow",
                "eyes",
                "glass",
                "hair",
                "hand",
                "hat",
                "mouth",
                "nose",
            )

//            for (i in titles.indices) {
//                listOptionEntity.add(
//                    PagerIconUI(
//                        context.getString(titles[i]),
//                        folders[i],
//                    )
//                )
//            }
//            _optionMutableLiveData.postValue(listOptionEntity)
        }
    }
}