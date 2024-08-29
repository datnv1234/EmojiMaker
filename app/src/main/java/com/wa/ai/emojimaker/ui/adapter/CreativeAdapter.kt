package com.wa.ai.emojimaker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.ItemCreativeBinding
import com.wa.ai.emojimaker.databinding.MenuOptionBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class CreativeAdapter (
    val itemClick:(pkg: PackageModel)->Unit,
    val optionClick: (binding: ItemCreativeBinding) -> Unit,
    val delete:(pkg: PackageModel) -> Unit,
    val rename:(pkg: PackageModel) -> Unit
) : BaseBindingAdapterDiff<PackageModel, ItemCreativeBinding>(
    object : DiffUtil.ItemCallback<PackageModel>() {
    override fun areItemsTheSame(oldItem: PackageModel, newItem: PackageModel): Boolean {
        return oldItem.id == newItem.id
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

    private var newPosition: Int = -1
        set(value) {
            oldPosition = field
            field = value
            notifyItemChanged(value)
        }
    var callBack: (Int, PackageModel) -> Unit = { _, _ -> }

    fun getCurrentPackage(): PackageModel? = currentList[newPosition]
    private var positionRename = -1

    override fun onBindViewHolderBase(holder: BaseHolder<ItemCreativeBinding>, position: Int) {
        val context = holder.binding.root.context
        with(getItem(holder.adapterPosition)) {
            holder.binding.apply {
                // Set View
                if (this@with.avatar != null) {
                    imgSticker.setImageBitmap(this@with.avatar)
                }
                tvCategory.text = getName()
                tvQuantity.text = itemSize.toString()

                // Action
                pkgView.setOnSafeClick {
                    itemClick(this@with)
                }

                btnOption.setOnSafeClick {
                    optionClick(this)
                    val inflater = LayoutInflater.from(context)
                    val view = MenuOptionBinding.inflate(inflater)
                    val popupWindow = PopupWindow(view.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                    view.btnDelete.setOnSafeClick {
                        delete(this@with)
                        popupWindow.dismiss()
                    }
                    view.btnRename.setOnSafeClick {
                        rename(this@with)
                        positionRename = position
                        popupWindow.dismiss()
                    }

                    view.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    val popupWidth = view.root.measuredWidth

                    popupWindow.isOutsideTouchable = true
                    popupWindow.isFocusable = true
                    popupWindow.setBackgroundDrawable(null)

                    popupWindow.showAsDropDown(btnOption, (-popupWidth * 1.5f).toInt(), 0)

                    newPosition = holder.adapterPosition
                }
                btnDelete.setOnSafeClick {
                    delete(this@with)
                }
            }

        }

    }

    override val layoutIdItem: Int
        get() = R.layout.item_creative

    fun updateFolderName(newName: String) {
        if (positionRename >= 0 && positionRename < currentList.size) {
            currentList[positionRename].updateName(newName)
            notifyItemChanged(positionRename)
            positionRename = -1
        }
    }

}