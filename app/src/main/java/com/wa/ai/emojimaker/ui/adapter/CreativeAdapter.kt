package com.wa.ai.emojimaker.ui.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.BitmapSticker
import com.wa.ai.emojimaker.databinding.ItemCreativeBinding
import com.wa.ai.emojimaker.databinding.ItemPieceStickerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class CreativeAdapter(val itemClick:(pos: Int)->Unit) : BaseBindingAdapterDiff<BitmapSticker, ItemCreativeBinding>(object : DiffUtil.ItemCallback<BitmapSticker>() {
    override fun areItemsTheSame(oldItem: BitmapSticker, newItem: BitmapSticker): Boolean {
        return oldItem.bitmap == newItem.bitmap
    }

    override fun areContentsTheSame(oldItem: BitmapSticker, newItem: BitmapSticker): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemCreativeBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            if (this.bitmap != null) {
                holder.binding.imgSticker.setImageBitmap(getItem(holder.adapterPosition).bitmap)
            }
            holder.binding.imgSticker.setOnSafeClick {
                itemClick(holder.adapterPosition)
            }
        }


        //Log.d(Constant.TAG, "StickerAdapter: " + getItem(holder.adapterPosition).bitmap)
    }

    override val layoutIdItem: Int
        get() = R.layout.item_creative
}