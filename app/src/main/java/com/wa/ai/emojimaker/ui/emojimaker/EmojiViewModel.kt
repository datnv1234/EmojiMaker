package com.wa.ai.emojimaker.ui.emojimaker

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.PagerIconUI
import com.wa.ai.emojimaker.data.model.PieceSticker
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EmojiViewModel : BaseViewModel() {
    private val _optionMutableLiveData: MutableLiveData<List<PagerIconUI>> = MutableLiveData()
    val optionMutableLiveData: LiveData<List<PagerIconUI>>
        get() = _optionMutableLiveData

    private val pieceOfAccessories = mutableListOf<PieceSticker>()
    private val pieceOfBeard = mutableListOf<PieceSticker>()
    private val pieceOfBrow = mutableListOf<PieceSticker>()
    private val pieceOfEyes = mutableListOf<PieceSticker>()
    private val pieceOfFace = mutableListOf<PieceSticker>()
    private val pieceOfGlass = mutableListOf<PieceSticker>()
    private val pieceOfHair = mutableListOf<PieceSticker>()
    private val pieceOfHand = mutableListOf<PieceSticker>()
    private val pieceOfHat = mutableListOf<PieceSticker>()
    private val pieceOfMouth = mutableListOf<PieceSticker>()
    private val pieceOfNose = mutableListOf<PieceSticker>()

    fun getItemOption(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            getPieceSticker(context)
            val listOptionEntity = mutableListOf<PagerIconUI>()
            val titles = listOf(
                R.string.face,
                R.string.eyes,
                R.string.nose,
                R.string.mouth,
                R.string.brow,
                R.string.beard,
                R.string.glass,
                R.string.hair,
                R.string.hat,
                R.string.hand,
                R.string.accessories)

            val folders = listOf(
                "face",
                "eyes",
                "nose",
                "mouth",
                "brow",
                "beard",
                "glass",
                "hair",
                "hat",
                "hand",
                "accessories",
            )
            val listOption = listOf(
                pieceOfFace,
                pieceOfEyes,
                pieceOfNose,
                pieceOfMouth,
                pieceOfBrow,
                pieceOfBeard,
                pieceOfGlass,
                pieceOfHair,
                pieceOfHat,
                pieceOfHand,
                pieceOfAccessories,
            )
            for (i in titles.indices) {
                listOptionEntity.add(
                    PagerIconUI(
                        context.getString(titles[i]),
                        folders[i],
                        listOption[i]
                    )
                )
            }
            _optionMutableLiveData.postValue(listOptionEntity)
            Log.d(Constant.TAG, "getCategoryList: " + _optionMutableLiveData.value?.size)
        }
    }

    private fun getPieceSticker(context: Context) {
        val assetManager = context.assets
        getFile(assetManager, "accessories", pieceOfAccessories)
        getFile(assetManager, "beard", pieceOfBeard)
        getFile(assetManager, "brow", pieceOfBrow)
        getFile(assetManager, "eyes", pieceOfEyes)
        getFile(assetManager, "face", pieceOfFace)
        getFile(assetManager, "glass", pieceOfGlass)
        getFile(assetManager, "hair", pieceOfHair)
        getFile(assetManager, "hand", pieceOfHand)
        getFile(assetManager, "hat", pieceOfHat)
        getFile(assetManager, "mouth", pieceOfMouth)
        getFile(assetManager, "nose", pieceOfNose)
    }

    private fun getFile(assetManager: AssetManager, category: String, list: MutableList<PieceSticker>) {
        val listFile = assetManager.list("item_options/$category/")
        if (listFile != null) {
            for (file in listFile) {
                val inputStream = assetManager.open("item_options/$category/$file")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                list.add(PieceSticker(bitmap))
                inputStream.close()
            }
        }
    }
}