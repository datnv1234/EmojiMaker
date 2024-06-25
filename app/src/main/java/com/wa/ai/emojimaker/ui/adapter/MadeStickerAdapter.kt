package com.wa.ai.emojimaker.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.databinding.ItemStickerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff

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
            val context = holder.itemView.context
            holder.binding.apply {
                Glide.with(context).load(path).into(imgSticker)
            }
        }
    }


}