package com.pixelbrew.qredi.ui.components.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.network.model.DownloadModel

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        const val USER_TOKEN = "user_token"
        const val LOAN_DATA = "loan_data"
    }


    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }


    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveLoanData(loanData: List<DownloadModel>) {
        val editor = prefs.edit()
        val json = gson.toJson(loanData)
        editor.putString(LOAN_DATA, json)
        editor.apply()
    }

    fun fetchLoanData(): List<DownloadModel>? {
        val json = prefs.getString(LOAN_DATA, null)
        return if (json != null) {
            val type = object : TypeToken<List<DownloadModel>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    fun clearLoanData() {
        val editor = prefs.edit()
        editor.remove(LOAN_DATA)
        editor.apply()
    }

    fun clearAll() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}