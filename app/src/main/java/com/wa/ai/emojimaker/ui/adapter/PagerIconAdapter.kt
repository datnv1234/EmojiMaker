package com.wa.ai.emojimaker.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PagerIconUI
import com.wa.ai.emojimaker.databinding.ItemPaperIconBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import timber.log.Timber

class PagerIconAdapter: BaseBindingAdapterDiff<PagerIconUI, ItemPaperIconBinding>(object : DiffUtil.ItemCallback<PagerIconUI>() {
    override fun areItemsTheSame(oldItem: PagerIconUI, newItem: PagerIconUI): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: PagerIconUI, newItem: PagerIconUI): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemPaperIconBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            holder.binding.tv.text = holder.adapterPosition.toString()
            holder.binding.rvIcon.adapter = this.stickerAdapter
        }

    }

    override val layoutIdItem: Int
        get() = R.layout.item_paper_icon
}