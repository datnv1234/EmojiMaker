package com.wa.ai.emojimaker.ui.dialog

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.databinding.DialogWaitingDialogBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingDialogFragment

class WaitingDialog(private val des: String) : BaseBindingDialogFragment<DialogWaitingDialogBinding>() {

    var action: (() -> Unit)? = null
    override val layoutId: Int
        get() = R.layout.dialog_waiting_dialog

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        binding.tvDes.text = des
        val countDownTimer: CountDownTimer = object : CountDownTimer(Constant.CREATE_STICKER_DELAY, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                action?.invoke()
                this@WaitingDialog.dismiss()
                //startActivity(intent)
            }
        }
        countDownTimer.start()
    }
}