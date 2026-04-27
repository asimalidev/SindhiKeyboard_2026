package com.sindhi.urdu.english.keybad.sindhikeyboard.jetpack_version.dbClasses

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.sindhi.urdu.english.keybad.sindhikeyboard.utils.DictionaryObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@SuppressLint("StaticFieldLeak")
object DataBaseCopyOperationsKt {

    private lateinit var myContext: Context
    private const val DATABASE_NAME = "dictionary_74.db"

    @Volatile
    private var isDatabaseCopied = false
    private var cachedDatabase: SQLiteDatabase? = null
    private val creationLock = Any()

    private val databasePath: String
        get() = myContext.getDatabasePath(DATABASE_NAME).path

    fun init(context: Context) {
        if (!::myContext.isInitialized) {
            myContext = context.applicationContext
            CoroutineScope(Dispatchers.IO).launch {
                ensureDatabaseExists()
            }
        }
    }

    private fun ensureDatabaseExists() {
        if (isDatabaseCopied) return

        val dbFile = File(databasePath)
        if (!dbFile.exists() || dbFile.length() == 0L) {
            synchronized(creationLock) {
                if (!dbFile.exists() || dbFile.length() == 0L) {
                    copyDataBase()
                }
            }
        }
        isDatabaseCopied = true
    }

    private fun copyDataBase() {
        try {
            val dbFile = File(databasePath)
            dbFile.parentFile?.mkdirs()

            myContext.assets.open(DATABASE_NAME).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }
            Log.d("DataBaseCopyOperations", "Database copied successfully.")
        } catch (e: Exception) {
            Log.e("DataBaseCopyOperations", "Error copying database", e)
        }
    }

    private fun <T> executeWithDatabase(action: (SQLiteDatabase) -> T): T? {
        if (!::myContext.isInitialized) {
            Log.e("DataBaseCopyOperations", "Context not initialized! Call init(context) first.")
            return null
        }

        ensureDatabaseExists()

        return try {
            if (cachedDatabase == null || cachedDatabase?.isOpen == false) {
                cachedDatabase = SQLiteDatabase.openDatabase(
                    databasePath,
                    null,
                    SQLiteDatabase.OPEN_READWRITE
                )
            }
            cachedDatabase?.let { action(it) }
        } catch (e: Exception) {
            Log.e("DataBaseCopyOperations", "DB execution failed", e)
            null
        }
    }

    // ==========================================
    //       SPECIFIC DATA ACCESS METHODS
    // ==========================================

    // THE FIX: This replaces getAllItems(). It ONLY loads words that match what the user is typing.
    suspend fun getSuggestionsForWord(searchWord: String, lang: String): List<SuggestionItems> = withContext(Dispatchers.IO) {
        val suggestionList = mutableListOf<SuggestionItems>()

        if (searchWord.isBlank()) return@withContext suggestionList

        executeWithDatabase { db ->
            val columnToSearch = if (lang == "English") "EngRomanWordsSuggestion" else "UrduWordsSuggestion"

            // Query limits to 50 results to strictly prevent OutOfMemoryError
            val query = "SELECT * FROM SuggestionList WHERE $columnToSearch LIKE ? LIMIT 50"
            val selectionArgs = arrayOf("$searchWord%")

            db.rawQuery(query, selectionArgs).use { cursor ->
                if (cursor.moveToFirst()) {
                    val idIndex = cursor.getColumnIndexOrThrow("id")
                    val engIndex = cursor.getColumnIndexOrThrow("EngRomanWordsSuggestion")
                    val urduIndex = cursor.getColumnIndexOrThrow("UrduWordsSuggestion")
                    val normIndex = cursor.getColumnIndexOrThrow("NormalSuggestion")
                    val dailyIndex = cursor.getColumnIndexOrThrow("DailyUseWords")

                    do {
                        suggestionList.add(
                            SuggestionItems(
                                id = cursor.getInt(idIndex),
                                engRomanWordsSuggestion = cursor.getString(engIndex),
                                urduWordsSuggestion = cursor.getString(urduIndex),
                                normalSuggestion = cursor.getString(normIndex),
                                dailyUseWords = cursor.getString(dailyIndex)
                            )
                        )
                    } while (cursor.moveToNext())
                }
            }
            Unit
        }
        suggestionList
    }

    fun getInAppPurchases(): Int {
        return executeWithDatabase { db ->
            var value = 0
            db.rawQuery("SELECT IN_APP_PURCHASES FROM common WHERE indexing = 1 LIMIT 1", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex("IN_APP_PURCHASES")
                    if (index != -1) value = cursor.getInt(index)
                }
            }
            value
        } ?: 0
    }

    fun updateInAppPurchases(newValue: Int) {
        executeWithDatabase { db ->
            val contentValues = ContentValues().apply { put("IN_APP_PURCHASES", newValue) }
            db.update("common", contentValues, "indexing = ?", arrayOf("1"))
        }
    }

    fun getRemoteConfigVisibility(): Int {
        return executeWithDatabase { db ->
            var value = 1
            db.rawQuery("SELECT KeyPadAdVisibility FROM common WHERE indexing = 1 LIMIT 1", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    value = cursor.getInt(cursor.getColumnIndexOrThrow("KeyPadAdVisibility"))
                }
            }
            value
        } ?: 1
    }

    fun updateRemoteConfigVisibility(newValue: Int) {
        executeWithDatabase { db ->
            val contentValues = ContentValues().apply { put("KeyPadAdVisibility", newValue) }
            db.update("common", contentValues, "indexing = ?", arrayOf("1"))
        }
    }

    fun getRemoteConfigAdmob(): Int {
        return executeWithDatabase { db ->
            var value = 0
            db.rawQuery("SELECT RemoteConfigAdmob FROM common WHERE indexing = 1 LIMIT 1", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    value = cursor.getInt(cursor.getColumnIndexOrThrow("RemoteConfigAdmob"))
                }
            }
            value
        } ?: 0
    }

    fun updateRemoteConfigAdmob(newValue: Int) {
        executeWithDatabase { db ->
            val contentValues = ContentValues().apply { put("RemoteConfigAdmob", newValue) }
            db.update("common", contentValues, "indexing = ?", arrayOf("1"))
        }
    }

    fun getRemoteConfigMintegral(): Int {
        return executeWithDatabase { db ->
            var value = 0
            db.rawQuery("SELECT RemoteConfigMintegral FROM common WHERE indexing = 1 LIMIT 1", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    value = cursor.getInt(cursor.getColumnIndexOrThrow("RemoteConfigMintegral"))
                }
            }
            value
        } ?: 0
    }

    fun updateRemoteConfigMintegral(newValue: Int) {
        executeWithDatabase { db ->
            val contentValues = ContentValues().apply { put("RemoteConfigMintegral", newValue) }
            db.update("common", contentValues, "indexing = ?", arrayOf("1"))
        }
    }

    fun getKeyPadBannerFailedShowTimeAdmob(): String {
        return executeWithDatabase { db ->
            var value = "0"
            db.rawQuery("SELECT KeyPadBannerFailedShowTimeAdmob FROM common WHERE indexing = 1 LIMIT 1", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    value = cursor.getString(cursor.getColumnIndexOrThrow("KeyPadBannerFailedShowTimeAdmob"))
                }
            }
            value
        } ?: "0"
    }

    fun updateKeyPadBannerFailedShowTimeAdmob(newValue: String) {
        executeWithDatabase { db ->
            val contentValues = ContentValues().apply { put("KeyPadBannerFailedShowTimeAdmob", newValue) }
            db.update("common", contentValues, "indexing = ?", arrayOf("1"))
        }
    }

    fun getKeyPadBannerFailedShowTimeMintegral(): String {
        return executeWithDatabase { db ->
            var value = "0"
            db.rawQuery("SELECT KeyPadBannerFailedShowTimeMintegral FROM common WHERE indexing = 1 LIMIT 1", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    value = cursor.getString(cursor.getColumnIndexOrThrow("KeyPadBannerFailedShowTimeMintegral"))
                }
            }
            value
        } ?: "0"
    }

    fun updateKeyPadBannerFailedShowTimeMintegral(newValue: String) {
        executeWithDatabase { db ->
            val contentValues = ContentValues().apply { put("KeyPadBannerFailedShowTimeMintegral", newValue) }
            db.update("common", contentValues, "indexing = ?", arrayOf("1"))
        }
    }
}


