package com.wa.ai.emojimaker.ui.component.main

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
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

    var suggestCategory: String? = null
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

    val categories: MutableList<Any> = ArrayList()

    private val _packageMutableLiveData: MutableLiveData<List<PackageModel>> = MutableLiveData()
    val packageMutableLiveData: LiveData<List<PackageModel>>
        get() = _packageMutableLiveData

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        timerReloadBanner?.cancel()
    }

    fun getCategories(context: Context) {
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

        cate.avatar1 = listFile[0]
        cate.avatar2 = listFile[1]
        cate.avatar3 = listFile[2]
        cate.avatar4 = listFile[3]
        return cate
    }

    fun getSuggestStickers(context: Context) {
        suggestCategory = listCategory.random()
        val assetManager = context.assets
        val listEntry = mutableListOf<MadeStickerModel>()
        viewModelScope.launch(Dispatchers.IO) {
            assetManager.list("categories/$suggestCategory")?.forEach {
                listEntry.add(
                    MadeStickerModel(
                        it,
                        suggestCategory,
                        path = "file:///android_asset/categories/$suggestCategory/$it"
                    )
                )
            }
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