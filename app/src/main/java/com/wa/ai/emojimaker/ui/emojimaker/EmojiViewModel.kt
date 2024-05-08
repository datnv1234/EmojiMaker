package com.wa.ai.emojimaker.ui.emojimaker

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PagerIconUI
import com.wa.ai.emojimaker.data.model.PieceSticker
import com.wa.ai.emojimaker.ui.adapter.StickerAdapter
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EmojiViewModel : BaseViewModel() {
    private val _optionMutableLiveData: MutableLiveData<List<PagerIconUI>> = MutableLiveData()
    val optionMutableLiveData: LiveData<List<PagerIconUI>>
        get() = _optionMutableLiveData

    private val pieceOfAccessories = mutableListOf<PieceSticker>()
    private val pieceOfBeard = mutableListOf<PieceSticker>()
    fun getItemOption(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            getPieceSticker(context)
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
            val stickerAdapter = StickerAdapter()
            stickerAdapter.submitList(pieceOfAccessories)
            val beardAdapter = StickerAdapter()
            stickerAdapter.submitList(pieceOfBeard)
            val listAdapter = listOf(
                stickerAdapter,
                beardAdapter,
                beardAdapter,
                beardAdapter,
                beardAdapter,
                beardAdapter,
                beardAdapter,
                beardAdapter,
                beardAdapter,
                beardAdapter,
                beardAdapter,
            )
            for (i in titles.indices) {
                listOptionEntity.add(
                    PagerIconUI(
                        context.getString(titles[i]),
                        folders[i],
                        listAdapter[i]
                    )
                )
            }
            _optionMutableLiveData.postValue(listOptionEntity)
        }
    }

    private fun getPieceSticker(context: Context) {
            val assetManager = context.assets
            val access = assetManager.list("accessories/")
            val beard = assetManager.list("beard/")
            if (access != null) {
                for (file in access) {
                    val inputStream = assetManager.open(file)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    pieceOfAccessories.add(PieceSticker(bitmap))
                    inputStream.close()
                }
            }
            if (beard != null) {
                for (file in beard) {
                    val inputStream = assetManager.open(file)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    pieceOfBeard.add(PieceSticker(bitmap))
                    inputStream.close()
                }
            }

    }
}