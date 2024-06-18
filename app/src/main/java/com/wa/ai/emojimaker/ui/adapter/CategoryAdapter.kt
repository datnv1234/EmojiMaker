package com.wa.ai.emojimaker.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.google.android.gms.ads.nativead.NativeAdView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.databinding.AdNativeVideoBinding
import com.wa.ai.emojimaker.databinding.ItemCategoryBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.ads.NativeAdsUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible
import java.lang.Exception

class CategoryAdapter(val context: Context, val watchMoreClick: (category: Category) -> Unit, val optionClick: (category: String) -> Unit) : BaseBindingAdapterDiff<Category, ItemCategoryBinding>(object : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.category == newItem.category
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemCategoryBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {

            if (this.itemSize == 0)    return
            holder.binding.btnOption.setOnSafeClick {
                optionClick(this.category.toString())
            }
            val text = "+" + (this.itemSize - 3).toString()
            holder.binding.tvRemainingNumber.text = text
            holder.binding.tvRemainingNumber.setOnSafeClick {
                watchMoreClick(this)
            }
            holder.binding.tvTitle.text = this.categoryName
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_category
}