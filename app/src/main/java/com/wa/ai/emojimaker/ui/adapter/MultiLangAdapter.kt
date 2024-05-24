package com.wa.ai.emojimaker.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.LanguageUI
import com.wa.ai.emojimaker.databinding.ItemMultiLangBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class MultiLangAdapter : BaseBindingAdapterDiff<LanguageUI, ItemMultiLangBinding>(
    object : DiffUtil.ItemCallback<LanguageUI>() {
        override fun areItemsTheSame(oldItem: LanguageUI, newItem: LanguageUI): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: LanguageUI, newItem: LanguageUI): Boolean {
            return oldItem == newItem
        }
    }
) {

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

    fun getCurrentLanguage() = currentList[newPosition]
    var callBack: (Int, LanguageUI) -> Unit = { _, _ -> }
    override fun onBindViewHolderBase(holder: BaseHolder<ItemMultiLangBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            holder.binding.apply {
                tvLanguage.text = name
                if (holder.adapterPosition == newPosition) {
                    avatar?.let {
                        radioBtn.isChecked = true
                        imgLanguage.setImageResource(it)
                    }

                } else {
                    avatar?.let {
                        radioBtn.isChecked = false
                        imgLanguage.setImageResource(it)
                    }
                }
                root.setOnSafeClick {
                    callBack(position, this@with)
                    newPosition = holder.adapterPosition
                }
            }
            setAnimation(holder.itemView, holder.adapterPosition, holder.itemView.context)

        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_multi_lang
}