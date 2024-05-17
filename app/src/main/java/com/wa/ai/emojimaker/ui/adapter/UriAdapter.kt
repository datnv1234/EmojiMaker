package com.wa.ai.emojimaker.ui.adapter

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant.TAG
import com.wa.ai.emojimaker.data.model.StickerUri
import com.wa.ai.emojimaker.databinding.ItemStickerBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import java.lang.Exception

class UriAdapter(val itemClick: () -> Unit) : BaseBindingAdapterDiff<StickerUri, ItemStickerBinding>(object : DiffUtil.ItemCallback<StickerUri>() {
    override fun areItemsTheSame(oldItem: StickerUri, newItem: StickerUri): Boolean {
        return oldItem.uri.toString() == newItem.uri.toString()
    }

    override fun areContentsTheSame(oldItem: StickerUri, newItem: StickerUri): Boolean {
        return oldItem == newItem
    }

}) {
    override val layoutIdItem: Int
        get() = R.layout.item_sticker

    override fun onBindViewHolderBase(holder: BaseHolder<ItemStickerBinding>, position: Int) {

        //Log.d(TAG, "onBindViewHolderBase:")
        with(getItem(holder.adapterPosition)) {
            download(this.uri, holder.binding.imgSticker)
            //Log.d(TAG, "onBindViewHolderBase: $this")
            holder.binding.imgSticker.setOnSafeClick {
                itemClick()
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
}