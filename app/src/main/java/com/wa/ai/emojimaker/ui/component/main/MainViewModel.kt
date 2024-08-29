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

    private val _loadBanner: MutableLiveData<Boolean> = MutableLiveData()
    val loadBanner: LiveData<Boolean>
        get() = _loadBanner

    private var timerReloadBanner : CountDownTimer? = null

    var suggestCategory: String? = null

    private val listCategory = listOf(
        "brainy_endeavors",
        "cat_chic",
        "cute_seal",
        "couple_emoji",
        "yellow_bow_moments",
        "blonde_expressions",
        "grim_reaper",
        "cute_girl",
        "dog_diversity",
        "axolotl_emotions",
        "animated_reaction",
        "koala_Chronicles",
        "cup_of_joy",
        "emoji",
        "funny_cat",
        "cuddly_cat_chronicles",
        "meow_tions",
        "pug_adventures",
        "axolotl_vibes",
        "funny_rat",
        "lazy_day_vibes",
        "anime_emotions",
        "orange_orchard",
        "pet_pawtentials",
        "emo_teens",
        "happy_hijabi",
        "quacking_quacks",
        "sly_spirits",
        "character_moods",
        "pink_personality",
        "happy_vibes",
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

    fun getCategories(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listEntity = mutableListOf<Any>()
            listEntity.add(getCategory(context, "pink_personality", "Pink Personality"))
            listEntity.add(getCategory(context, "anime_emotions", "Anime Emotions"))
            listEntity.add(getCategory(context, "meow_tions", "Meow_tions"))
            listEntity.add(getCategory(context, "happy_vibes", "Happy Vibes"))
            listEntity.add(getCategory(context, "cat_chic", "Cat Chic"))
            listEntity.add(getCategory(context, "happy_hijabi", "Happy Hijabi"))
            listEntity.add(getCategory(context, "koala_chronicles", "Koala Chronicles"))
            listEntity.add(getCategory(context, "cute_seal", "Cute Seal"))
            listEntity.add(getCategory(context, "yellow_bow_moments", "Yellow Bow"))
            listEntity.add(getCategory(context, "emo_teens", "Emo Teens"))
            listEntity.add(getCategory(context, "orange_orchard", "Orange Orchard"))
            listEntity.add(getCategory(context, "funny_rat", "Funny rat"))
            listEntity.add(getCategory(context, "funny_cat", "Funny Cat"))
            listEntity.add(getCategory(context, "cute_girl", "Cute girl"))
            listEntity.add(getCategory(context, "lazy_day_vibes", "Lazy Day Vibes"))
            listEntity.add(getCategory(context, "pet_pawtentials", "Pet Pawtentials"))
            listEntity.add(getCategory(context, "axolotl_emotions", "Axolotl Emotions"))
            listEntity.add(getCategory(context, "sly_spirits", "Sly Spirits"))
            listEntity.add(getCategory(context, "xiximi", "Xiximi"))
            listEntity.add(getCategory(context, "dog_diversity", "Dog Diversity"))
            listEntity.add(getCategory(context, "pug_adventures", "Pug Adventures"))
            listEntity.add(getCategory(context, "quacking_quacks", "Quacking Quacks"))
            listEntity.add(getCategory(context, "emoji", "Emoji"))
            listEntity.add(getCategory(context, "brainy_endeavors", "Brainy Endeavors"))
            listEntity.add(getCategory(context, "couple_emoji", "Couple emoji"))
            listEntity.add(getCategory(context, "character_moods", "Character Moods"))
            listEntity.add(getCategory(context, "cup_of_joy", "Cup Of Joy"))
            listEntity.add(getCategory(context, "cuddly_cat_chronicles", "Cuddly Cat Chronicles"))
            listEntity.add(getCategory(context, "axolotl_vibes", "Axolotl Vibes"))
            listEntity.add(getCategory(context, "grim_reaper", "Grim Reaper"))
            listEntity.add(getCategory(context, "blonde_expressions", "Blonde Expressions"))
            listEntity.add(getCategory(context, "animated_reaction", "Animated Reaction"))

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