package com.itiro.jp_notify

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream

class KanjiDatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    data class Kanji(
        val caractere: String,
        val significado: String
    )

    companion object {
        private const val DATABASE_NAME = "jlpt_words.sqlite"
        private const val DATABASE_VERSION = 1
    }

    init {
        copyDatabaseIfNeeded()
    }

    private fun copyDatabaseIfNeeded() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (!dbFile.exists()) {
            context.assets.open(DATABASE_NAME).use { input ->
                dbFile.parentFile?.mkdirs()
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getRandomKanjiByLevels(levels: List<String>): Kanji {
        val db = readableDatabase
        val placeholders = levels.joinToString(",") { "?" }
        val query =
            "SELECT word, reading, meaning FROM jlpt_data WHERE jlpt_level IN ($placeholders) ORDER BY RANDOM() LIMIT 1"
        val cursor = db.rawQuery(query, levels.toTypedArray())
        var kanji = Kanji("?", "No data")

        if (cursor.moveToFirst()) {
            val word = cursor.getString(0)
            val reading = cursor.getString(1)
            val meaning = cursor.getString(2)
            val display = if (!word.isNullOrEmpty()) "$word ($reading)" else reading ?: "?"
            kanji = Kanji(display, meaning ?: "")
        }
        cursor.close()
        return kanji
    }
}
