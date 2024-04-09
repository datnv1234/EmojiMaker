package com.wa.ai.emojimaker.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.MessageEvent
import org.greenrobot.eventbus.EventBus

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        EventBus.getDefault().post(MessageEvent(Constant.EVENT_NET_WORK_CHANGE))
    }
}