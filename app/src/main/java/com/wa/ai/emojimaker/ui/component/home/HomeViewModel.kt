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
}