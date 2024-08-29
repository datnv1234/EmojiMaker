package com.wa.ai.emojimaker.ui.component.emojimerge.result

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.ai.emojimaker.data.model.CollectionUI
import com.wa.ai.emojimaker.data.repo.collection.CollectionRepo
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MergeResultVM @Inject constructor(private val collectionRepo: CollectionRepo) : BaseViewModel() {

    private val _bitmapMutableLiveData: MutableLiveData<Bitmap> = MutableLiveData()
    val bitmapMutableLiveData: LiveData<Bitmap>
        get() = _bitmapMutableLiveData

    fun insertEmotToCollection(collectionUI : CollectionUI) =
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepo.insertCollection(collectionUI).flowOn(Dispatchers.IO)
                .collectLatest { idLang ->
                     kotlin.runCatching {
                         collectionUI.apply {
                             id = idLang.toInt()
                         }
                     }.onFailure {
                         it.printStackTrace()
                     }
                }
        }

    fun setBitmap(bitmap: Bitmap) {
        _bitmapMutableLiveData.postValue(bitmap)
    }
}