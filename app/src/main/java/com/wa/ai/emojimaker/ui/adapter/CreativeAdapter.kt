package com.wa.ai.emojimaker.ui.adapter

import android.content.Context
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.PackageModel
import com.wa.ai.emojimaker.databinding.ItemCreativeBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class CreativeAdapter (
    val context: Context,
    val itemClick:(pkg: PackageModel)->Unit,
    val optionClick: (binding: ItemCreativeBinding) -> Unit,
    val delete:(pkg: PackageModel) -> Unit
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

    override fun onBindViewHolderBase(holder: BaseHolder<ItemCreativeBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            holder.binding.apply {
                // Set View
                if (avatar != null) {
                    imgSticker.setImageBitmap(avatar)
                }
                tvCategory.text = name
                tvQuantity.text = itemSize.toString()

                // Action
                pkgView.setOnSafeClick {
                    itemClick(this@with)
                }

                btnOption.setOnSafeClick {
                    optionClick(this)
                    val wrapper = ContextThemeWrapper(context, R.style.CustomPopupMenu)
                    val popUp = PopupMenu(wrapper, it)
                    popUp.menuInflater.inflate(R.menu.popup_menu, popUp.menu)
                    //popUp.gravity = Gravity.END
                    popUp.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_delete -> {
                                delete(this@with)
                            }
                        }
                        true
                    }
                    popUp.show()
                    newPosition = holder.adapterPosition
                }
                btnDelete.setOnSafeClick {
                    delete(this@with)
                }
            }

        }
        //Log.d(Constant.TAG, "StickerAdapter: " + getItem(holder.adapterPosition).bitmap)
    }

    override val layoutIdItem: Int
        get() = R.layout.item_creative

    /*private fun expand() {
        expandableLayout.visibility = View.VISIBLE
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        expandableLayout.measure(widthSpec, heightSpec)
        val animator = ObjectAnimator.ofInt(expandableLayout, "height", 0, expandableLayout.measuredHeight)
        animator.duration = 300
        animator.start()
        toggleButton.text = "Collapse"
    }

    private fun collapse() {
        val initialHeight = expandableLayout.measuredHeight
        val animator = ObjectAnimator.ofInt(expandableLayout, "height", initialHeight, 0)
        animator.duration = 300
        animator.start()
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator?) {
                expandableLayout.visibility = View.GONE
            }
        })
        toggleButton.text = "Expand"
    }*/
}