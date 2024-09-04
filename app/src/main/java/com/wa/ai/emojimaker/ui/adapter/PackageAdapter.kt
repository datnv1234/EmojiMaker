package com.wa.ai.emojimaker.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.ItemPackageBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class PackageAdapter : BaseBindingAdapterDiff<PackageModel, ItemPackageBinding>(object : DiffUtil.ItemCallback<PackageModel>() {
    override fun areItemsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem.getName() == newItem.getName()
    }

    override fun areContentsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem == newItem
    }

}) {

    private var oldPosition: Int = 0
        set(value) {
            field = value
            notifyItemChanged(value)
        }

    private var newPosition: Int = 0
        set(value) {
            oldPosition = field
            field = value
            notifyItemChanged(value)
        }
    var callBack: (Int, PackageModel) -> Unit = { _, _ -> }

    fun getCurrentPackage(): PackageModel? = currentList[newPosition]

    override fun onBindViewHolderBase(holder: BaseHolder<ItemPackageBinding>, position: Int) {
        with(getItem(position)) {
            if (this.avatar != null) {
                holder.binding.imgAvatar.setImageBitmap(this.avatar)
            }
            holder.binding.tvPackageName.text = this.getName()
            holder.binding.apply {

            rdSelectPackage.isChecked = position == newPosition
            root.setOnSafeClick {
                callBack(position, this@with)
                newPosition = position
            }
            }
        }

    }

    override val layoutIdItem: Int
        get() = R.layout.item_package
}