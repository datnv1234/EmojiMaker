package com.wa.ai.emojimaker.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PieceSticker
import com.wa.ai.emojimaker.databinding.ItemPieceStickerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class StickerAdapter(val itemClick:(pos: Int)->Unit) : BaseBindingAdapterDiff<PieceSticker, ItemPieceStickerBinding>(object : DiffUtil.ItemCallback<PieceSticker>() {
    override fun areItemsTheSame(oldItem: PieceSticker, newItem: PieceSticker): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: PieceSticker, newItem: PieceSticker): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemPieceStickerBinding>, position: Int) {
        val context = holder.itemView.context
        holder.binding.apply {
            Glide.with(context).load(
                "file:///android_asset/item_options/" +
                        "${getItem(position).category}/" +
                        getItem(position).name
            ).into(piece)
        }
        holder.binding.piece.setOnSafeClick {
            itemClick(position)
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_piece_sticker
}