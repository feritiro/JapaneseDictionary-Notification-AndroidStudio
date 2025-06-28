package com.itiro.jp_notify

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.widget.RemoteViews

class MainActivity : AppCompatActivity() {

    private val channelId = "kanji_channel"
    private val notificationId = 101
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Solicitar permissão para notificações em Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }

        createNotificationChannel()

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            showNotification("漢", "Significa 'Han' (dinastia chinesa)")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
            } else {
                // Permissão negada - avise o usuário se quiser
            }
        }
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
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(titulo: String, descricao: String) {
        val notificationLayout = RemoteViews(packageName, R.layout.notification_custom)

        notificationLayout.setTextViewText(R.id.kanjiTextView, titulo)
        notificationLayout.setTextViewText(R.id.meaningTextView, descricao)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(notificationLayout) // modo compacto
            .setCustomBigContentView(notificationLayout) // modo expandido
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}
