package com.wa.ai.emojimaker.ui.component.main

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel : BaseViewModel() {

    var category: String? = null
    var categorySize: Int = 0
    private var timerReloadBanner : CountDownTimer? = null

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

    private val _loadBanner: MutableLiveData<Boolean> = MutableLiveData()
    val loadBanner: LiveData<Boolean>
        get() = _loadBanner
    val categories: MutableList<Any> = ArrayList()

    private val _packageMutableLiveData: MutableLiveData<List<PackageModel>> = MutableLiveData()
    val packageMutableLiveData: LiveData<List<PackageModel>>
        get() = _packageMutableLiveData

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        timerReloadBanner?.cancel()
    }

    fun getCategoryList(context: Context) {
        viewModelScope.launch {
            categories.add(getCategory(context, "cat_chic", "Cat Chic"))
            categories.add(getCategory(context, "orange_orchard", "Orange Orchard"))
            categories.add(getCategory(context, "funny_rat", "Funny rat"))
            categories.add(getCategory(context, "dog_diversity", "Dog Diversity"))
            categories.add(getCategory(context, "pet_pawtentials", "Pet Pawtentials"))
            categories.add(getCategory(context, "sly_spirits", "Sly Spirits"))
            categories.add(getCategory(context, "xiximi", "Xiximi"))
            categories.add(getCategory(context, "funny_cat", "Funny Cat"))
            categories.add(getCategory(context, "quacking_quacks", "Quacking Quacks"))
            categories.add(getCategory(context, "emoji", "Emoji"))
            categories.add(getCategory(context, "brainy_endeavors", "Brainy Endeavors"))
            categories.add(getCategory(context, "couple_emoji", "Couple emoji"))
            categories.add(getCategory(context, "cute_girl", "Cute girl"))
        }
    }

    private fun getCategory(context: Context, category: String, categoryName: String) : Category {
        val assetManager = context.assets
        val cate = Category(category, categoryName, 0)
        val listFile = assetManager.list("categories/$category") ?: return cate
        cate.itemSize = listFile.size
        if (listFile.isEmpty()) return cate
        val inputStream1 = assetManager.open("categories/$category/${listFile[0]}")
        val bitmap1 = BitmapFactory.decodeStream(inputStream1)
        cate.avatar1 = bitmap1
        inputStream1.close()
        val inputStream2 = assetManager.open("categories/$category/${listFile[1]}")
        val bitmap2 = BitmapFactory.decodeStream(inputStream2)
        cate.avatar2 = bitmap2
        inputStream2.close()
        val inputStream3 = assetManager.open("categories/$category/${listFile[2]}")
        val bitmap3 = BitmapFactory.decodeStream(inputStream3)
        cate.avatar3 = bitmap3
        inputStream3.close()
        val inputStream4 = assetManager.open("categories/$category/${listFile[3]}")
        val bitmap4 = BitmapFactory.decodeStream(inputStream4)
        cate.avatar4 = bitmap4
        inputStream4.close()
        return cate
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

    fun getPackage(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listEntry = mutableListOf<PackageModel>()
            val cw = ContextWrapper(context)
            val directory: File = cw.getDir(Constant.INTERNAL_MY_CREATIVE_DIR, Context.MODE_PRIVATE)
            val files = directory.listFiles()      // Get packages
            if (files != null) {                    //package's size > 0
                for (file in files) {
                    if (file.isDirectory) {
                        var bitmap : Bitmap? = null
                        val item = file.listFiles()
                        if (item != null) {
                            if (item.isNotEmpty()) {
                                bitmap = AppUtils.convertFileToBitmap(item.random())
                            }
                        }
                        val name = file.name.replace("_", " ")
                        listEntry.add(PackageModel(name, item?.size ?: 0, bitmap))
                    }
                }
                _packageMutableLiveData.postValue(listEntry)
            }
        }
    }

    fun addPackage(pkg: PackageModel) {
        val listEntry = mutableListOf<PackageModel>()
        packageMutableLiveData.value?.let {
            listEntry.addAll(it)
        }

        listEntry.add(0, pkg)
        _packageMutableLiveData.postValue(listEntry)
    }

    fun removePackage(pkg: PackageModel) {
        val listEntry = mutableListOf<PackageModel>()
        packageMutableLiveData.value?.let {
            listEntry.addAll(it)
        }
        listEntry.remove(pkg)
        _packageMutableLiveData.postValue(listEntry)
    }

}