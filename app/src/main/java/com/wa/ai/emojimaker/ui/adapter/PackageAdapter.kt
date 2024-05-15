package com.wa.ai.emojimaker.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.ItemPackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff

class PackageAdapter : BaseBindingAdapterDiff<PackageModel, ItemPackageBinding>(object : DiffUtil.ItemCallback<PackageModel>() {
    override fun areItemsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemPackageBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            holder.binding.imgAvatar.setImageBitmap(this.avatar)
            holder.binding.tvPackageName.text = this.name
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_package
}