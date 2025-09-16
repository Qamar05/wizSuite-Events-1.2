package com.wizsuite.event.utils

import android.content.Context

class PreferenceUtils {
    companion object {
        private val preferenceName = "com.wizsuite.crompton"
        fun saveBoolean(context: Context, key: String?, value: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun getBoolean(context: Context, key: String?): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(key, false)
        }

        fun saveInt(context: Context, key: String?, value: Int) {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun getInt(context: Context, key: String?): Int {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            return sharedPreferences.getInt(key, 0)
        }

        fun saveFloat(context: Context, key: String?, value: Float) {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putFloat(key, value)
            editor.apply()
        }

        fun getFloat(context: Context, key: String?): Float {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            return sharedPreferences.getFloat(key, 0.0f)
        }

        fun saveLong(context: Context, key: String?, value: Long) {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong(key, value)
            editor.apply()
        }

        fun getLong(context: Context, key: String?): Long {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            return sharedPreferences.getLong(key, 0L)
        }

        fun saveString(context: Context, key: String?, value: String?) {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getString(context: Context, key: String?): String? {
            val sharedPreferences =
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, null)
        }
    }
}