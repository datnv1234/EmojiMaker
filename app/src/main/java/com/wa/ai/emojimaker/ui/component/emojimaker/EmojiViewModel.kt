package com.wa.ai.emojimaker.ui.component.emojimaker

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.ItemOptionUI
import com.wa.ai.emojimaker.data.model.PagerIconUI
import com.wa.ai.emojimaker.data.model.PieceSticker
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiMakerActivity.Companion.LOCK1
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiMakerActivity.Companion.LOCK2
import com.wa.ai.emojimaker.ui.component.emojimaker.EmojiMakerActivity.Companion.LOCK3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EmojiViewModel : BaseViewModel() {

    private val _loadBanner: MutableLiveData<Boolean> = MutableLiveData()
    val loadBanner: LiveData<Boolean>
        get() = _loadBanner

    private var timerReloadBanner : CountDownTimer? = null

    private val _pageSelected: MutableLiveData<Int> = MutableLiveData()
    val pageSelected: LiveData<Int>
        get() = _pageSelected

    private val _lock1: MutableLiveData<Boolean> = MutableLiveData()
    val lock1: LiveData<Boolean>
        get() = _lock1

    private val _lock2: MutableLiveData<Boolean> = MutableLiveData()
    val lock2: LiveData<Boolean>
        get() = _lock2

    private val _lock3: MutableLiveData<Boolean> = MutableLiveData()
    val lock3: LiveData<Boolean>
        get() = _lock3

    init {
        _lock1.postValue(true)
        _lock2.postValue(true)
        _lock3.postValue(true)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        timerReloadBanner?.cancel()
        optionList.clear()
    }

    val optionList = ArrayList<ItemOptionUI>()

    private val _bitmapMutableLiveData: MutableLiveData<Bitmap> = MutableLiveData()
    val bitmapMutableLiveData: LiveData<Bitmap>
        get() = _bitmapMutableLiveData

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

    fun setBitmap(bitmap: Bitmap) {
        _bitmapMutableLiveData.postValue(bitmap)
    }

    fun setPageSelected(position: Int) {
        _pageSelected.postValue(position)
    }
    fun setLock(position: Int, isLock: Boolean) {
        when (position) {
            LOCK1 -> {
                _lock1.postValue(isLock)
                _pageSelected.postValue(position)
            }
            LOCK2 -> {
                _lock2.postValue(isLock)
                _pageSelected.postValue(position)
            }
            LOCK3 -> {
                _lock3.postValue(isLock)
                _pageSelected.postValue(position)
            }
            else -> {
                _lock1.postValue(false)
                _lock2.postValue(false)
                _lock3.postValue(false)
                _pageSelected.postValue(position)
            }
        }
    }

    private fun getOptions() {
        optionList.add(ItemOptionUI("face", R.drawable.ic_face))
        optionList.add(ItemOptionUI("eyes", R.drawable.ic_eyes))
        optionList.add(ItemOptionUI("nose", R.drawable.ic_nose))
        optionList.add(ItemOptionUI("mouth", R.drawable.ic_mouth))
        optionList.add(ItemOptionUI("brow", R.drawable.ic_brow))
        optionList.add(ItemOptionUI("beard", R.drawable.ic_beard))
        optionList.add(ItemOptionUI("glass", R.drawable.ic_glass))
        optionList.add(ItemOptionUI("hair", R.drawable.ic_hair))
        optionList.add(ItemOptionUI("hat", R.drawable.ic_hat))
        optionList.add(ItemOptionUI("hand", R.drawable.ic_hand))
        optionList.add(ItemOptionUI("accessories", R.drawable.ic_accessory))
    }


    fun getItemOption(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {

            getOptions()
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
                R.string.accessories
            )

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

    private fun getFile(
        assetManager: AssetManager,
        category: String,
        list: MutableList<PieceSticker>
    ) {
        assetManager.list("item_options/$category")?.forEach {
            list.add(PieceSticker(category, it))
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