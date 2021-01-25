package com.example.currencyapp.data.prefs

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson

class PreferencesService(
    private val appContext: Application, private var prefs: SharedPreferences,
    private val gson: Gson
) {

    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun getLong(key: String): Long? {
        return prefs.getLong(key, 0)
    }

    fun <T> setValue(key: String, value: T, shouldEncrypt: Boolean) {
        val editor = prefs.edit()

        when (value) {
            is Boolean -> editor.putBoolean(key, value as Boolean)
            is String -> editor.putString(key, value as String)
            is Long -> editor.putLong(key, value as Long)
        }

        editor.apply()
    }
}