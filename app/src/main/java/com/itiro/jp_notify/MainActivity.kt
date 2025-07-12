package com.itiro.jp_notify

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val channelId = "kanji_channel"
    private val notificationId = 101
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1001

    private lateinit var dbHelper: KanjiDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Permissão para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }

        createNotificationChannel()

        dbHelper = KanjiDatabaseHelper(this)

        // Botão para testar manualmente
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            showRandomKanjiNotification()
        }

        // 🔁 Registra o receiver para ACTION_SCREEN_OFF
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(ScreenOffReceiver(), filter)
    }

    private fun showRandomKanjiNotification() {
        val kanji = dbHelper.getRandomKanji()

        val notificationLayout = RemoteViews(packageName, R.layout.notification_custom)
        notificationLayout.setTextViewText(R.id.kanjiTextView, kanji.caractere)
        notificationLayout.setTextViewText(R.id.meaningTextView, kanji.significado)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(notificationLayout)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificações de Kanji"
            val descriptionText = "Flashcards na tela de bloqueio"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
