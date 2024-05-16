package com.wa.ai.emojimaker.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.LanguageUI
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.ItemPackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class PackageAdapter : BaseBindingAdapterDiff<PackageModel, ItemPackageBinding>(object : DiffUtil.ItemCallback<PackageModel>() {
    override fun areItemsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem == newItem
    }

}) {

    private var oldPosition: Int = -1
        set(value) {
            field = value
            notifyItemChanged(value)
        }

    var newPosition: Int = -1
        set(value) {
            oldPosition = field
            field = value
            notifyItemChanged(value)
        }
    var callBack: (Int, PackageModel) -> Unit = { _, _ -> }

    fun getCurrentPackage() = currentList[newPosition]

    override fun onBindViewHolderBase(holder: BaseHolder<ItemPackageBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            if (this.avatar != null) {
                holder.binding.imgAvatar.setImageBitmap(this.avatar)
            }
            holder.binding.tvPackageName.text = this.name
            holder.binding.apply {

                rdSelectPackage.isChecked = holder.adapterPosition == newPosition
                root.setOnSafeClick {
                    callBack(position, this@with)
                    newPosition = holder.adapterPosition
                }
            }
        }

    }

    override val layoutIdItem: Int
        get() = R.layout.item_package
}