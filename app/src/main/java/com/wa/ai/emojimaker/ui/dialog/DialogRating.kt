package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.data.local.SharedPreferenceHelper
import com.wa.ai.emojimaker.databinding.DialogRatingBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment
import com.wa.ai.emojimaker.utils.extention.setOnSafeClick
import com.wa.ai.emojimaker.utils.extention.visible

class DialogRating : BaseBindingDialogFragment<DialogRatingBinding>() {

	var onClickFiveStar: (() -> Unit) = {}
	var onRating: (() -> Unit) = {}

	var onDismiss: () -> Unit = {}

	override val layoutId: Int
		get() = R.layout.dialog_rating

	override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
		onclick()
		changeRating()
		binding.btnRate.text = context?.getString(R.string.rate)
		binding.imgIcon.setImageResource(R.drawable.img_rating_default)
		binding.ratingBar.rating = 5f
	}

	private fun savePrefData() {
		SharedPreferenceHelper.storeBoolean(Constant.KEY_IS_RATE, true)
	}

	private fun changeRating() {
		binding.ratingBar.setOnRatingChangeListener { _, rating, _ ->
			when (rating.toString()) {
				"1.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_1)
					binding.tvContent.text =
						context?.getString(R.string.content_dialog_rating_1_2_3)
					binding.imgIcon.setImageResource(R.drawable.img_rating_1)
				}

				"2.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_1)
					binding.tvContent.text =
						context?.getString(R.string.content_dialog_rating_1_2_3)
					binding.imgIcon.setImageResource(R.drawable.img_rating_2)
				}

				"3.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_1)
					binding.tvContent.text =
						context?.getString(R.string.content_dialog_rating_1_2_3)
					binding.imgIcon.setImageResource(R.drawable.img_rating_3)
				}

				"4.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_5)
					binding.tvContent.text = context?.getString(R.string.content_dialog_rating_4_5)
					binding.imgIcon.setImageResource(R.drawable.img_rating_4)
				}

				"5.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_5)
					binding.tvContent.text = context?.getString(R.string.content_dialog_rating_4_5)
					binding.imgIcon.setImageResource(R.drawable.img_rating_5)
				}

				else -> {
					binding.tvTitle.text = context?.getString(R.string.title_rate_default)
					binding.tvContent.text =
						context?.getString(R.string.content_rate_default)
					binding.imgIcon.setImageResource(R.drawable.img_rating_default)
				}
			}
		}
	}


	private fun onclick() {
		binding.btnRate.setOnSafeClick {
			if (binding.ratingBar.rating == 0f) {
				toast(getString(R.string.please_feedback))
			} else {
				binding.imgIcon.visible()
				if (binding.ratingBar.rating == 5f) {
					savePrefData()
					toast(getString(R.string.thank_you))
					onClickFiveStar()
				} else {
					toast(getString(R.string.thank_you))
					onRating()
				}
			}
		}
		binding.tvExit.setOnSafeClick {
			dismiss()
		}
	}
}