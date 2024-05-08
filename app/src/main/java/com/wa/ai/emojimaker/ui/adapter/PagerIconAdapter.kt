package com.wa.ai.emojimaker.ui.adapter

import android.graphics.Bitmap
import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PagerIconUI
import com.wa.ai.emojimaker.databinding.ItemPaperIconBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff

class PagerIconAdapter(val itemClick:(bitmap: Bitmap) -> Unit): BaseBindingAdapterDiff<PagerIconUI, ItemPaperIconBinding>(object : DiffUtil.ItemCallback<PagerIconUI>() {
    override fun areItemsTheSame(oldItem: PagerIconUI, newItem: PagerIconUI): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: PagerIconUI, newItem: PagerIconUI): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemPaperIconBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            val stickerAdapter = StickerAdapter(itemClick = {
                itemClick(listPieces[it].bitmap)
            })
            stickerAdapter.submitList(this.listPieces)
            holder.binding.rvIcon.adapter = stickerAdapter
        }

    }

    override val layoutIdItem: Int
        get() = R.layout.item_paper_icon
}