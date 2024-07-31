package com.wa.ai.emojimaker.ui.component.main

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.os.CountDownTimer
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAdView
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

    private val _loadBanner: MutableLiveData<Boolean> = MutableLiveData()
    val loadBanner: LiveData<Boolean>
        get() = _loadBanner

    private var timerReloadBanner : CountDownTimer? = null

    var suggestCategory: String? = null

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

    private val _categoriesMutableLiveData: MutableLiveData<List<Any>> = MutableLiveData()
    val categoriesMutableLiveData: LiveData<List<Any>>
        get() = _categoriesMutableLiveData

    private val _packageMutableLiveData: MutableLiveData<List<PackageModel>> = MutableLiveData()
    val packageMutableLiveData: LiveData<List<PackageModel>>
        get() = _packageMutableLiveData

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        timerReloadBanner?.cancel()
    }

    fun getAdWidth(context: Context) : Int {
        val displayMetrics = context.resources.displayMetrics
        val adWidthPixels =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
                windowMetrics.bounds.width()
            } else {
                displayMetrics.widthPixels
            }
        val density = displayMetrics.density
        return (adWidthPixels / density).toInt()
    }
    fun getCategories(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listEntity = mutableListOf<Any>()
            val adView1 = NativeAdView(context)
            listEntity.add(adView1)
            listEntity.add(getCategory(context, "cat_chic", "Cat Chic"))
            listEntity.add(getCategory(context, "orange_orchard", "Orange Orchard"))
            listEntity.add(getCategory(context, "funny_rat", "Funny rat"))
            listEntity.add(getCategory(context, "dog_diversity", "Dog Diversity"))
            val adView2 = NativeAdView(context)
            listEntity.add(adView2)
            listEntity.add(getCategory(context, "pet_pawtentials", "Pet Pawtentials"))
            listEntity.add(getCategory(context, "sly_spirits", "Sly Spirits"))
            listEntity.add(getCategory(context, "xiximi", "Xiximi"))
            listEntity.add(getCategory(context, "funny_cat", "Funny Cat"))
            val adView3 = NativeAdView(context)
            listEntity.add(adView3)
            listEntity.add(getCategory(context, "quacking_quacks", "Quacking Quacks"))
            listEntity.add(getCategory(context, "emoji", "Emoji"))
            listEntity.add(getCategory(context, "brainy_endeavors", "Brainy Endeavors"))
            listEntity.add(getCategory(context, "couple_emoji", "Couple emoji"))
            val adView4 = NativeAdView(context)
            listEntity.add(adView4)
            listEntity.add(getCategory(context, "cute_girl", "Cute girl"))
            _categoriesMutableLiveData.postValue(listEntity)
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