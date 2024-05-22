package com.wa.ai.emojimaker.ui.adapter

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.databinding.ItemCategoryBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import java.lang.Exception

class CategoryAdapter(val watchMoreClick: (category: Category) -> Unit, val optionClick: (category: String) -> Unit) : BaseBindingAdapterDiff<Category, ItemCategoryBinding>(object : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.category == newItem.category
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onBindViewHolderBase(holder: BaseHolder<ItemCategoryBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            holder.binding.btnOption.setOnSafeClick {
                optionClick(this.category.toString())
            }
            val text = "+" + (this.itemSize - 3).toString()
            holder.binding.tvRemainingNumber.text = text
            holder.binding.tvRemainingNumber.setOnSafeClick {
                watchMoreClick(this)
            }
            holder.binding.tvTitle.text = this.categoryName
            if (avatar1 != null)
                holder.binding.imgPreview1.setImageBitmap(avatar1)
            if (avatar2 != null)
                holder.binding.imgPreview2.setImageBitmap(avatar2)
            if (avatar3 != null)
                holder.binding.imgPreview3.setImageBitmap(avatar3)
            if (avatar4 != null)
                holder.binding.imgPreview4.setImageBitmap(avatar4)
        }
    }

    private fun download(uri: Uri, imageView: ImageView) {
        Picasso.get().load(uri)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception?) {
                }
            })
    }

    override val layoutIdItem: Int
        get() = R.layout.item_category
}