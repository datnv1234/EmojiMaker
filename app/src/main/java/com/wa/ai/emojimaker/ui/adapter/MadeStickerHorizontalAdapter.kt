package com.wa.ai.emojimaker.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.MadeStickerModel
import com.wa.ai.emojimaker.databinding.ItemStickerBinding
import com.wa.ai.emojimaker.databinding.ItemStickerHorizontalBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class MadeStickerHorizontalAdapter : BaseBindingAdapterDiff<MadeStickerModel, ItemStickerHorizontalBinding>(object : DiffUtil.ItemCallback<MadeStickerModel>() {
    override fun areItemsTheSame(oldItem: MadeStickerModel, newItem: MadeStickerModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: MadeStickerModel, newItem: MadeStickerModel): Boolean {
        return oldItem == newItem
    }

}) {

    private var focusedItemPosition: Int = -1

    override val layoutIdItem: Int
        get() = R.layout.item_sticker_horizontal

    override fun onBindViewHolderBase(holder: BaseHolder<ItemStickerHorizontalBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            val context = holder.itemView.context
            holder.binding.apply {
                Glide.with(context).load(path).into(imgSticker)
            }

            holder.binding.imgSticker.setOnSafeClick {
                onItemFocus(holder.adapterPosition)
            }

            holder.binding.imgSticker.isSelected = position == focusedItemPosition
        }
    }

    private fun onItemFocus(pos: Int) {
        val previousFocusedItem = focusedItemPosition
        focusedItemPosition = pos

        if (previousFocusedItem >= 0) {
            notifyItemChanged(previousFocusedItem)
        }
        notifyItemChanged(focusedItemPosition)
    }
}