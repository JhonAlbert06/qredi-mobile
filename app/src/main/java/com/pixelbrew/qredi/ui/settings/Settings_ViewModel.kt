package com.pixelbrew.qredi.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
class SettingsViewModel(
    private val sessionManager: SessionManager,
    private val context: Context
) : ViewModel() {

    private val _printerName = MutableLiveData<String>()
    val printerName: LiveData<String> get() = _printerName

    private val _apiUrl = MutableLiveData<String>()
    val apiUrl: LiveData<String> get() = _apiUrl

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _pairedDevices = MutableLiveData<List<BluetoothDevice>>()
    val pairedDevices: LiveData<List<BluetoothDevice>> get() = _pairedDevices

    private val _selectedDevice = MutableLiveData<BluetoothDevice?>()
    val selectedDevice: LiveData<BluetoothDevice?> get() = _selectedDevice

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> get() = _isDarkTheme

    init {
        try {
            _printerName.value = sessionManager.fetchPrinterName() ?: ""
            _apiUrl.value = sessionManager.fetchApiUrl() ?: ""
            refreshPairedDevices()
        } catch (e: Exception) {
            _printerName.value = ""
            _apiUrl.value = ""
            Log.e("SettingsViewModel", "Error al cargar configuración", e)
        }
    }

    fun refreshPairedDevices() {
        loadPairedDevices()
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun onDeviceSelected(device: BluetoothDevice?) {
        _selectedDevice.postValue(device)
        _printerName.postValue(device?.name ?: "")
    }

    fun onPrinterNameChange(name: String) {
        _printerName.postValue(name)
    }

    fun onApiUrlChange(url: String) {
        _apiUrl.postValue(url)
    }

    fun onThemeChange(isDark: Boolean) {
        _isDarkTheme.postValue(isDark)
        sessionManager.saveDarkTheme(isDark)
    }

    fun saveSettings() {
        sessionManager.savePrinterName(_printerName.value ?: "")
        sessionManager.saveApiUrl(_apiUrl.value ?: "")
        _toastMessage.value = "Configuración guardada"
    }

    private fun loadPairedDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val bluetoothManager =
                    context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

                if (bluetoothAdapter == null) {
                    withContext(Dispatchers.Main) {
                        _toastMessage.value = "Bluetooth no está disponible en este dispositivo"
                    }
                    return@launch
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    withContext(Dispatchers.Main) {
                        _toastMessage.value = "Permiso de Bluetooth no concedido"
                    }
                    return@launch
                }

                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
                withContext(Dispatchers.Main) {
                    _pairedDevices.value = pairedDevices?.toList() ?: emptyList()
                    Log.d("SettingsViewModel", "Dispositivos emparejados: ${_pairedDevices.value}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _toastMessage.value = "Error al cargar dispositivos emparejados"
                    Log.e("SettingsViewModel", "Error al cargar dispositivos emparejados", e)
                }
            }
        }
    }
}