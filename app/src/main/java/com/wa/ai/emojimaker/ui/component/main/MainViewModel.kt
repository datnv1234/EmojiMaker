package com.wa.ai.emojimaker.ui.component.main

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
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

    private val _categoriesMutableLiveData: MutableLiveData<List<Category>> = MutableLiveData()
    val categoriesMutableLiveData: LiveData<List<Category>>
        get() = _categoriesMutableLiveData

    private val _packageMutableLiveData: MutableLiveData<List<PackageModel>> = MutableLiveData()
    val packageMutableLiveData: LiveData<List<PackageModel>>
        get() = _packageMutableLiveData

    fun getCategoryList(context: Context) {
        val assetManager = context.assets
        viewModelScope.launch(Dispatchers.IO) {
            val listEntry = mutableListOf<Category>()
            listEntry.add(getCategory(assetManager,"cat_chic", "Cat Chic"))
            listEntry.add(getCategory(assetManager, "orange_orchard", "Orange Orchard"))
            listEntry.add(getCategory(assetManager, "funny_rat", "Funny rat"))
            listEntry.add(getCategory(assetManager, "dog_diversity", "Dog Diversity"))
            listEntry.add(getCategory(assetManager, "pet_pawtentials", "Pet Pawtentials"))
            listEntry.add(getCategory(assetManager, "sly_spirits", "Sly Spirits"))
            listEntry.add(getCategory(assetManager, "xiximi", "Xiximi"))
            listEntry.add(getCategory(assetManager, "funny_cat", "Funny Cat"))
            listEntry.add(getCategory(assetManager, "quacking_quacks", "Quacking Quacks"))
            listEntry.add(getCategory(assetManager, "emoji", "Emoji"))
            listEntry.add(getCategory(assetManager, "brainy_endeavors", "Brainy Endeavors"))
            listEntry.add(getCategory(assetManager, "couple_emoji", "Couple emoji"))
            listEntry.add(getCategory(assetManager, "cute_girl", "Cute girl"))

            _categoriesMutableLiveData.postValue(listEntry)
        }
    }
    private fun getCategory(assetManager: AssetManager, category: String, categoryName: String) : Category {
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