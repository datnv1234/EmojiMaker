//package com.wa.ai.emojimaker.service.notification
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.view.View
//import android.widget.RemoteViews
//import androidx.core.app.NotificationCompat
//import com.wa.ai.emojimaker.App
//import com.wa.ai.emojimaker.R
//
//object NotificationCreator {
//    private const val FOREGROUND_CHANNEL_ID = "foreground_channel_id"
//    const val notificationId = 1094
//    var mNotificationManager: NotificationManager? = null
//    private var notification: Notification? = null
//    private var remoteViews: RemoteViews? = null
//    private var mBuilderCurrent: NotificationCompat.Builder? = null
//    fun getNotification(context: Context): Notification? {
//        mNotificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (mNotificationManager!!.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
//                val name: CharSequence =
//                    "Start service" //context.getResources().getString("Notifi");
//                val importance = NotificationManager.IMPORTANCE_LOW
//                val channel = NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance)
//                channel.enableVibration(false)
//                mNotificationManager!!.createNotificationChannel(channel)
//            }
//        }
//        val notificationIntent: Intent = App.instance.getPackageManager()
//            .getLaunchIntentForPackage(App.instance.getPackageName())?.setPackage(null) ?:
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED )
//        var pendingIntent: PendingIntent? = null
//        pendingIntent = PendingIntent.getActivity(
//            context, 0, notificationIntent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//        remoteViews = RemoteViews(context.packageName, R.layout.layout_notification_service)
//        remoteViews!!.setViewVisibility(R.id.tvContentNoti, View.VISIBLE)
//        remoteViews!!.setOnClickPendingIntent(R.id.lnOverlayWindow, pendingIntent)
//        remoteViews!!.setTextViewText(R.id.tvContentNoti, "Your privacy is being protected")
//        val notificationBuilder: NotificationCompat.Builder =
//            NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
//                .setContent(remoteViews)
//                .setSmallIcon(R.drawable.icon_logo)
//                .setCategory(NotificationCompat.CATEGORY_SERVICE)
//                .setOnlyAlertOnce(true)
//                .setOngoing(true)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//        notification = notificationBuilder.build()
//        mBuilderCurrent = notificationBuilder
//        mNotificationManager!!.notify(notificationId, notification)
//        return notification
//    }
//
//    fun updateRemoteView(context: Context) {
//        remoteViews = RemoteViews(context.packageName, R.layout.layout_notification_service)
//        remoteViews!!.setTextViewText(R.id.tvContentNoti, "Your privacy is being protected")
//        mBuilderCurrent!!.setContent(remoteViews)
//        mNotificationManager!!.notify(notificationId, mBuilderCurrent!!.build())
//    }
//}
