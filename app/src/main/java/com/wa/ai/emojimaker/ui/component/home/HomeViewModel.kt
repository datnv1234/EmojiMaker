package com.wa.ai.emojimaker.ui.component.home

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    var stickerUri = ArrayList<Uri>()

    private val _categoriesMutableLiveData: MutableLiveData<List<Category>> = MutableLiveData()

    private val _categoriesSearchedMutableLiveData: MutableLiveData<List<Category>> = MutableLiveData()


    val categoriesSearchedMutableLiveData: LiveData<List<Category>>
        get() = _categoriesSearchedMutableLiveData

    val categoriesMutableLiveData: LiveData<List<Category>>
        get() = _categoriesMutableLiveData

    fun getSearchedData(tx: String) {
        val listEntry = mutableListOf<Category>()
        for (cate in _categoriesMutableLiveData.value!!) {
            if (cate.category?.contains(tx) == true || tx == "") {
                listEntry.add(cate)
            }
        }
        _categoriesSearchedMutableLiveData.postValue(listEntry)
    }
    fun getCategoryList(context: Context) {
        val assetManager = context.assets
        viewModelScope.launch(Dispatchers.IO) {
            val listEntry = mutableListOf<Category>()
            listEntry.add(getCategory(assetManager,"cat_chic", "Cat Chic"))
            listEntry.add(getCategory(assetManager, "orange_orchard", "Orange Orchard"))
            listEntry.add(getCategory(assetManager, "funny_cat", "Funny rat"))
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
        val listFile = assetManager.list("categories/$category/")
        cate.itemSize = listFile?.size!!

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
}