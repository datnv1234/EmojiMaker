package com.wa.ai.emojimaker.ui.component.mycreative

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import com.wa.ai.emojimaker.utils.AppUtils.convertFileToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MyCreativeViewModel : BaseViewModel() {

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

    private val _packageMutableLiveData: MutableLiveData<List<PackageModel>> = MutableLiveData()
    val packageMutableLiveData: LiveData<List<PackageModel>>
        get() = _packageMutableLiveData

    private val _stickerMutableLiveData: MutableLiveData<List<MadeStickerModel>> = MutableLiveData()
    val stickerMutableLiveData: LiveData<List<MadeStickerModel>>
        get() = _stickerMutableLiveData

    fun getStickers(context: Context) {
        category = listCategory.random()
        val assetManager = context.assets
        val listEntry = mutableListOf<MadeStickerModel>()
        viewModelScope.launch(Dispatchers.IO) {
            val listFile = assetManager.list("categories/$category/")

            if (listFile != null) {
                for ((i, file) in listFile.withIndex()) {
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
                                bitmap = convertFileToBitmap(item.random())
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

        listEntry.add(pkg)
        _packageMutableLiveData.postValue(listEntry)
    }



}