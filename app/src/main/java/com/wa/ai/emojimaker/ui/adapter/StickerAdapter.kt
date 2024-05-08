package com.wa.ai.emojimaker.ui.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.PieceSticker
import com.wa.ai.emojimaker.databinding.ItemPieceStickerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff

class StickerAdapter : BaseBindingAdapterDiff<PieceSticker, ItemPieceStickerBinding>(object : DiffUtil.ItemCallback<PieceSticker>() {
    override fun areItemsTheSame(oldItem: PieceSticker, newItem: PieceSticker): Boolean {
        return oldItem.bitmap == newItem.bitmap
    }

    override fun areContentsTheSame(oldItem: PieceSticker, newItem: PieceSticker): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemPieceStickerBinding>, position: Int) {
        holder.binding.piece.setImageBitmap(getItem(holder.adapterPosition).bitmap)
        Log.d(Constant.TAG, "StickerAdapter: " + getItem(holder.adapterPosition).bitmap)
    }

    override val layoutIdItem: Int
        get() = R.layout.item_piece_sticker
}