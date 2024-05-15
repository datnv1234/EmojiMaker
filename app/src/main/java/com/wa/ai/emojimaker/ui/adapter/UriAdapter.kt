package com.wa.ai.emojimaker.ui.adapter

import android.net.Uri
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.Category
import com.wa.ai.emojimaker.databinding.ItemCategoryBinding
import com.wa.ai.emojimaker.databinding.ItemStickerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import java.lang.Exception

class UriAdapter : BaseBindingAdapterDiff<Uri, ItemStickerBinding>(object : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem == newItem
    }

}) {
    override val layoutIdItem: Int
        get() = R.layout.item_sticker

    override fun onBindViewHolderBase(holder: BaseHolder<ItemStickerBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            download(this, holder.binding.sticker)
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
}