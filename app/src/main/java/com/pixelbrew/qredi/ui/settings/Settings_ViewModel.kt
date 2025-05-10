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
import com.google.gson.Gson
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.ApiError
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.data.network.model.RouteModelRes
import com.pixelbrew.qredi.data.network.model.SpentTypeModel
import com.pixelbrew.qredi.data.network.model.SpentTypeModelRes
import com.pixelbrew.qredi.data.network.model.UserModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _printerName = MutableLiveData<String>("")
    val printerName: LiveData<String> get() = _printerName

    private val _apiUrl = MutableLiveData<String>("")
    val apiUrl: LiveData<String> get() = _apiUrl

    private val _pairedDevices = MutableLiveData<List<BluetoothDevice>>()
    val pairedDevices: LiveData<List<BluetoothDevice>> get() = _pairedDevices

    private val _selectedDevice = MutableLiveData<BluetoothDevice?>()
    val selectedDevice: LiveData<BluetoothDevice?> get() = _selectedDevice

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private val _routes = MutableLiveData<List<RouteModelRes>>()
    val routesList: LiveData<List<RouteModelRes>> get() = _routes

    private val _spentTypes = MutableLiveData<List<SpentTypeModelRes>>()
    val spentTypesList: LiveData<List<SpentTypeModelRes>> get() = _spentTypes

    init {
        try {
            getRoutes()
            _printerName.value = sessionManager.fetchPrinterName() ?: ""
            _apiUrl.value = sessionManager.fetchApiUrl() ?: ""
            refreshPairedDevices()
            Log.d(
                "SettingsViewModel",
                "Inicializado con printerName=${_printerName.value}, apiUrl=${_apiUrl.value}"
            )
        } catch (e: Exception) {
            _printerName.value = ""
            _apiUrl.value = ""
            Log.e("SettingsViewModel", "Error al cargar configuración inicial", e)
        }
    }

    fun getSpentTypes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val spentTypesUrl = "${sessionManager.fetchApiUrl()}/spent/type"
                val response = apiService.getSpentTypes(spentTypesUrl)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _spentTypes.value = response.body() ?: emptyList()
                        Log.d("API_RESPONSE", "Fetched spent types: ${_spentTypes.value?.size}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Fetch spent types error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Exception fetching spent types: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error de red al obtener tipos de gasto: ${e.message}")
                }
            }
        }
    }

    fun createSpentType(spentTypeName: String) {
        val spentType = SpentTypeModel()
        spentType.name = spentTypeName

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${sessionManager.fetchApiUrl()}/spent/type"
                val response = apiService.createSpentType(url, spentType)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val createdSpentType = response.body()
                        showToast("Tipo de gasto ${createdSpentType.toString()} creado con éxito")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Create spent type error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Exception creating spent type: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error de red al crear el tipo de gasto: ${e.message}")
                }
            }
        }
    }

    fun createRoute(routeName: String) {
        val route = RouteModel()
        route.name = routeName
        route.companyId = sessionManager.fetchUser()?.company?.id ?: ""

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${sessionManager.fetchApiUrl()}/route"
                val response = apiService.createRoute(url = url, route = route)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val createdRoute = response.body()
                        showToast("Ruta ${createdRoute.toString()} creada con éxito")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Create route error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Exception creating route: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error de red al crear la ruta: ${e.message}")
                }
            }
        }
    }

    fun getRoutes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val routesUrl = "${sessionManager.fetchApiUrl()}/routes"
                val response = apiService.getRoutes(routesUrl)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _routes.value = response.body() ?: emptyList()
                        Log.d("API_RESPONSE", "Fetched routes: ${_routes.value?.size}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Fetch routes error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Exception fetching routes: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error de red al obtener rutas: ${e.message}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        _toastMessage.postValue(Event(message))
    }

    fun refreshPairedDevices() {
        loadPairedDevices()
    }

    fun getUser(): UserModel? {
        val user = sessionManager.fetchUser()
        if (user == null) {
            Log.w("SettingsViewModel", "getUser() devolvió null")
        }
        return user
    }

    fun reloadSettings() {
        _printerName.value = sessionManager.fetchPrinterName()
        _apiUrl.value = sessionManager.fetchApiUrl()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun onDeviceSelected(device: BluetoothDevice?) {
        _selectedDevice.postValue(device)
        _printerName.postValue(device?.name ?: "")
        Log.d("SettingsViewModel", "Dispositivo seleccionado: ${device?.name}")
    }

    fun onPrinterNameChange(name: String) {
        _printerName.postValue(name)
        Log.d("SettingsViewModel", "PrinterName cambiado: $name")
    }

    fun onApiUrlChange(url: String) {
        _apiUrl.postValue(url)
        Log.d("SettingsViewModel", "ApiUrl cambiado: $url")
    }

    fun saveSettings() {
        sessionManager.savePrinterName(_printerName.value ?: "")
        sessionManager.saveApiUrl(_apiUrl.value ?: "")
        showToast("Configuración guardada")
        Log.d(
            "SettingsViewModel",
            "Configuración guardada: printerName=${_printerName.value}, apiUrl=${_apiUrl.value}"
        )
    }

    private fun loadPairedDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bluetoothManager =
                    context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

                if (bluetoothAdapter == null) {
                    withContext(Dispatchers.Main) {
                        showToast("Bluetooth no está disponible en este dispositivo")
                        Log.w("SettingsViewModel", "BluetoothAdapter es null")
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
                        showToast("Permiso de Bluetooth no concedido")
                        Log.w("SettingsViewModel", "Permiso BLUETOOTH_CONNECT no concedido")
                    }
                    return@launch
                }

                val pairedDevices = bluetoothAdapter.bondedDevices
                withContext(Dispatchers.Main) {
                    _pairedDevices.value = pairedDevices?.toList() ?: emptyList()
                    Log.d(
                        "SettingsViewModel",
                        "Dispositivos emparejados: ${_pairedDevices.value?.size}"
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error al cargar dispositivos emparejados")
                    Log.e("SettingsViewModel", "Error al cargar dispositivos emparejados", e)
                }
            }
        }
    }
}
