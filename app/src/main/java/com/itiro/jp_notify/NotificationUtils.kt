package com.itiro.jp_notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager

object NotificationUtils {
    private const val channelId = "kanji_channel"
    private const val notificationId = 101

    fun sendKanjiNotification(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val selectedLevels = mutableListOf<String>()
        if (prefs.getBoolean("n1", false)) selectedLevels.add("N1")
        if (prefs.getBoolean("n2", false)) selectedLevels.add("N2")
        if (prefs.getBoolean("n3", false)) selectedLevels.add("N3")
        if (prefs.getBoolean("n4", false)) selectedLevels.add("N4")
        if (prefs.getBoolean("n5", false)) selectedLevels.add("N5")
        if (selectedLevels.isEmpty()) return // Nada selecionado, não envia notificação

        val dbHelper = KanjiDatabaseHelper(context)
        val kanji = dbHelper.getRandomKanjiByLevels(selectedLevels)

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_custom)
        notificationLayout.setTextViewText(R.id.kanjiTextView, kanji.caractere)
        notificationLayout.setTextViewText(R.id.meaningTextView, kanji.significado)

        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificações de Kanji"
            val descriptionText = "Flashcards na tela de bloqueio"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
