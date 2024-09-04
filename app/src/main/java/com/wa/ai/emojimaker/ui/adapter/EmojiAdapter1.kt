package com.wa.ai.emojimaker.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.EmojiUI
import com.wa.ai.emojimaker.databinding.EmojisSliderItemBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick

class EmojiAdapter1 : BaseBindingAdapterDiff<EmojiUI, EmojisSliderItemBinding>(
    object : DiffUtil.ItemCallback<EmojiUI>() {
        override fun areItemsTheSame(oldItem: EmojiUI, newItem: EmojiUI): Boolean {
            return oldItem.emojiName == newItem.emojiName
        }

        override fun areContentsTheSame(oldItem: EmojiUI, newItem: EmojiUI): Boolean {
            return oldItem == newItem
        }
    }
) {

    var callBack: (Int, EmojiUI) -> Unit = { _, _ -> }
    override fun onBindViewHolderBase(holder: BaseHolder<EmojisSliderItemBinding>, position: Int) {
        val context = holder.itemView.context
        with(getItem(position)) {
            val emojiURL =
                "https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis_png/$emojiUnicode.png"
            holder.binding.apply {
                loadEmojiFromUrl(emoji, progressBar, emojiURL, context)
                root.setOnSafeClick {
                    callBack(position, this@with)
                }
            }
        }
    }

    private fun loadEmojiFromUrl(
        image: ImageView,
        progressBar: CircularProgressIndicator,
        url: String,
        context: Context,
    ) {
        Glide.with(context)
            .load(url)
            .listener(
                object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                }
            )
            .into(image)
    }

    override val layoutIdItem: Int
        get() = R.layout.emojis_slider_item
}