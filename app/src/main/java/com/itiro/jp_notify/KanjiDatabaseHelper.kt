package com.itiro.jp_notify

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream

data class Kanji(
    val caractere: String,
    val significado: String
)

class KanjiDatabaseHelper(private val context: Context) {

    private val databaseName = "jlpt_words.sqlite"
    private val databasePath: String = context.getDatabasePath(databaseName).path
    private var database: SQLiteDatabase? = null

    init {
        copyDatabaseIfNeeded()
    }

    private fun copyDatabaseIfNeeded() {
        val dbFile = File(databasePath)
        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            context.assets.open(databaseName).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("KanjiDatabaseHelper", "Database copied to $databasePath")
        }
    }

    fun openDatabase() {
        if (database == null || !database!!.isOpen) {
            database = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY)
        }
    }

    fun closeDatabase() {
        database?.close()
    }

    fun getRandomKanji(): Kanji {
        openDatabase()
        val cursor = database!!.rawQuery("SELECT word, reading, meaning FROM jlpt_data ORDER BY RANDOM() LIMIT 1", null)

        val kanji = if (cursor.moveToFirst()) {
            val word = cursor.getString(0)
            val reading = cursor.getString(1)
            val meaning = cursor.getString(2)

            // Se word estiver vazio ou nulo, usa o reading
            val displayWord = if (word.isNullOrEmpty()) reading else word

            Kanji(displayWord ?: "?", meaning ?: "No meaning")
        } else {
            Kanji("?", "No data")
        }

        cursor.close()
        return kanji
    }

}
