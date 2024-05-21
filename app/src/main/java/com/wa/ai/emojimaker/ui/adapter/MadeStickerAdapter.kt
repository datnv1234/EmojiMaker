package com.wa.ai.emojimaker.ui.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.databinding.ItemStickerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import java.lang.Exception

class MadeStickerAdapter(val itemClick: () -> Unit) : BaseBindingAdapterDiff<MadeStickerModel, ItemStickerBinding>(object : DiffUtil.ItemCallback<MadeStickerModel>() {
    override fun areItemsTheSame(oldItem: MadeStickerModel, newItem: MadeStickerModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: MadeStickerModel, newItem: MadeStickerModel): Boolean {
        return oldItem == newItem
    }

}) {
    override val layoutIdItem: Int
        get() = R.layout.item_sticker

    override fun onBindViewHolderBase(holder: BaseHolder<ItemStickerBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            holder.binding.imgSticker.setImageBitmap(this.bitmap)
            //Log.d(TAG, "onBindViewHolderBase: ${this.name}")
            holder.binding.imgSticker.setOnSafeClick {
                itemClick()
            }
        }
    }


}