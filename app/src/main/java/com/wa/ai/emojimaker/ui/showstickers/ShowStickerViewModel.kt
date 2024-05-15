package com.wa.ai.emojimaker.ui.showstickers

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.wa.ai.emojimaker.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowStickerViewModel : BaseViewModel() {

    private val _stickersMutableLiveData: MutableLiveData<List<Uri>> = MutableLiveData()

    val categoriesMutableLiveData: LiveData<List<Uri>>
        get() = _stickersMutableLiveData

    fun getStickers(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listEntry = mutableListOf<Uri>()
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child(category)
            storageRef.listAll().addOnSuccessListener {
                for (item in it.items) {
                    item.downloadUrl.addOnSuccessListener { uri ->
                        listEntry.add(uri)
                    }
                }
            }
            _stickersMutableLiveData.postValue(listEntry)
        }
    }
}