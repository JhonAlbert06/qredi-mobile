package com.pixelbrew.qredi.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixelbrew.qredi.ui.components.services.SessionManager

class SettingsViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _printerName = MutableLiveData<String>()
    val printerName: MutableLiveData<String> get() = _printerName

    private val _apiUrl = MutableLiveData<String>()
    val apiUrl: MutableLiveData<String> get() = _apiUrl

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    init {
        _printerName.value = sessionManager.fetchPrinterName()
        _apiUrl.value = sessionManager.fetchApiUrl()
    }

    fun onPrinterNameChange(name: String) {
        _printerName.value = name
    }

    fun onApiUrlChange(url: String) {
        _apiUrl.value = url
    }

    fun saveSettings() {
        sessionManager.savePrinterName(_printerName.value ?: "")
        sessionManager.saveApiUrl(_apiUrl.value ?: "")
        _toastMessage.value = "Configuración guardada"
    }

}