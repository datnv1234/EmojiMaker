package com.wa.ai.emojimaker.ui.adapter

import androidx.appcompat.content.res.AppCompatResources
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.ItemOptionUI
import com.wa.ai.emojimaker.databinding.ItemOptionsBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapter
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import timber.log.Timber

class OptionAdapter(
    private val optionList: ArrayList<ItemOptionUI>,
    private val itemClick: (pos: Int) -> Unit
) : BaseBindingAdapter<ItemOptionsBinding>() {

    override fun onBindViewHolderBase(holder: BaseHolder<ItemOptionsBinding>, position: Int) {
        val item = optionList[position]
        holder.binding.tvOption.text = item.title
        val context = holder.binding.root.context
        try {
            holder.binding.imgOption.setImageDrawable(
                AppCompatResources.getDrawable(context, item.icon)
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
        holder.binding.item.setOnSafeClick {
            itemClick(position)
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_options

    override fun getItemCount(): Int {
        return optionList.size
    }

}