package com.wa.ai.emojimaker.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.data.model.IntroUI
import com.wa.ai.emojimaker.databinding.ItemIntroBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingAdapterDiff

class IntroAdapter : BaseBindingAdapterDiff<IntroUI, ItemIntroBinding>(
	object : DiffUtil.ItemCallback<IntroUI>() {
		override fun areItemsTheSame(oldItem: IntroUI, newItem: IntroUI): Boolean {
			return oldItem.title == newItem.title
		}

		override fun areContentsTheSame(oldItem: IntroUI, newItem: IntroUI): Boolean {
			return oldItem == newItem
		}

	}
) {
	override fun onBindViewHolderBase(holder: BaseHolder<ItemIntroBinding>, position: Int) {
		with(getItem(position)) {
			Glide.with(holder.itemView).load(icon).into(holder.binding.imgIntro)
			holder.binding.tvTitle.text = title ?: ""
			holder.binding.tvContent.text = content ?: ""
		}
	}

	override val layoutIdItem: Int
		get() = R.layout.item_intro
}