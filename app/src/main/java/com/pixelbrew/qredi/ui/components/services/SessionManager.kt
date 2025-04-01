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

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER = "user"
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