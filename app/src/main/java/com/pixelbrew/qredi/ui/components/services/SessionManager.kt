package com.pixelbrew.qredi.ui.components.services

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.network.model.UserModel

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    private val gson = Gson()

    init {
        savePrinterName("2C-P58-C")
        saveApiUrl("http://192.168.1.10:3000")
        saveAuthToken("")
    }

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER = "user"
        const val PRINTER_NAME = "printer_name"
        const val API_URL = "api_url"
    }

    fun savePrinterName(printerName: String) {
        prefs.edit() {
            putString(PRINTER_NAME, printerName)
        }
    }

    fun fetchPrinterName(): String? {
        return prefs.getString(PRINTER_NAME, null)
    }

    fun saveApiUrl(apiUrl: String) {
        prefs.edit() {
            putString(API_URL, apiUrl)
        }
    }

    fun fetchApiUrl(): String? {
        return prefs.getString(API_URL, null)
    }

    fun saveAuthToken(token: String) {
        prefs.edit() {
            putString(USER_TOKEN, token)
        }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUser(user: UserModel) {
        val userJson = gson.toJson(user)
        prefs.edit() {
            putString(USER, userJson)
        }
    }

    fun fetchUser(): UserModel? {
        val userJson = prefs.getString(USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, UserModel::class.java)
        } else {
            null
        }
    }

}