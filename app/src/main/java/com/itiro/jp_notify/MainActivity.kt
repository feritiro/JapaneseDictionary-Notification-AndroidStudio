package com.itiro.jp_notify

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_POST_NOTIFICATIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }

        registerReceiver(ScreenOffReceiver(), IntentFilter(Intent.ACTION_SCREEN_OFF))

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val levels = listOf("n1", "n2", "n3", "n4", "n5")
        levels.forEach { level ->
            val checkBoxId = resources.getIdentifier("checkbox_${level}", "id", packageName)
            val cb = findViewById<CheckBox>(checkBoxId)
            cb.isChecked = prefs.getBoolean(level, false)
            cb.setOnCheckedChangeListener { _, isChecked ->
                prefs.edit().putBoolean(level, isChecked).apply()
            }
        }
    }
}
