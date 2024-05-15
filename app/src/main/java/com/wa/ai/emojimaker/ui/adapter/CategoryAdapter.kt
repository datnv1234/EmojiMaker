package com.wa.ai.emojimaker.ui.adapter

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant.TAG
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
            holder.binding.tvRemainingNumber.setOnSafeClick {
                watchMoreClick(this)
            }
            holder.binding.tvTitle.text = this.categoryName
            getPreView(holder, this.category.toString())
        }
    }

    private fun getPreView(holder: BaseHolder<ItemCategoryBinding>, category: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(category)
        storageRef.listAll().addOnSuccessListener {
            val item0 = it.items[0]
            val item1 = it.items[1]
            val item2 = it.items[2]
            val item3 = it.items[3]
            val size = "+" + (it.items.size - 3).toString()
            holder.binding.tvRemainingNumber.text = size
            item0.downloadUrl.addOnSuccessListener { uri ->
                download(uri, holder.binding.imgPreview1)
            }
            item1.downloadUrl.addOnSuccessListener { uri ->
                download(uri, holder.binding.imgPreview2)
            }
            item2.downloadUrl.addOnSuccessListener { uri ->
                download(uri, holder.binding.imgPreview3)
            }
            item3.downloadUrl.addOnSuccessListener { uri ->
                download(uri, holder.binding.imgPreview4)
            }
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