package com.wa.ai.emojimaker.ui.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.model.BitmapSticker
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.ItemCreativeBinding
import com.wa.ai.emojimaker.databinding.ItemPieceStickerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class CreativeAdapter(val itemClick:(pos: Int)->Unit) : BaseBindingAdapterDiff<PackageModel, ItemCreativeBinding>(object : DiffUtil.ItemCallback<PackageModel>() {
    override fun areItemsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemCreativeBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            if (this.avatar != null) {
                holder.binding.imgSticker.setImageBitmap(this.avatar)
            }
            holder.binding.imgSticker.setOnSafeClick {
                itemClick(holder.adapterPosition)
            }
            holder.binding.tvCategory.text = this.name
            holder.binding.tvQuantity.text = this.itemSize.toString()
        }


        //Log.d(Constant.TAG, "StickerAdapter: " + getItem(holder.adapterPosition).bitmap)
    }

    override val layoutIdItem: Int
        get() = R.layout.item_creative
}