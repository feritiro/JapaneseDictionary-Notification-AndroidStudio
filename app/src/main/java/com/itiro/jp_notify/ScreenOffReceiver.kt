package com.itiro.jp_notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import android.app.NotificationManager

class ScreenOffReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            val dbHelper = KanjiDatabaseHelper(context)
            val kanji = dbHelper.getRandomKanji()

            val notificationLayout = RemoteViews(context.packageName, R.layout.notification_custom)
            notificationLayout.setTextViewText(R.id.kanjiTextView, kanji.caractere)
            notificationLayout.setTextViewText(R.id.meaningTextView, kanji.significado)

            val builder = NotificationCompat.Builder(context, "kanji_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCustomContentView(notificationLayout)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(102, builder.build())
        }
    }
}
