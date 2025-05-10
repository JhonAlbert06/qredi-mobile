package com.pixelbrew.qredi.ui.spent

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.ApiError
import com.pixelbrew.qredi.data.network.model.SpentModel
import com.pixelbrew.qredi.data.network.model.SpentModelRes
import com.pixelbrew.qredi.data.network.model.SpentTypeModelRes
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SpentViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val apiService: ApiService
) : ViewModel() {

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private val _spentTypes = MutableLiveData<List<SpentTypeModelRes>>()
    val spentTypesList: LiveData<List<SpentTypeModelRes>> get() = _spentTypes

    private val _spents = MutableLiveData<List<SpentModelRes>>()
    val spentsList: LiveData<List<SpentModelRes>> get() = _spents

    private val _newSpent = MutableLiveData<SpentModel>()
    val newSpent: LiveData<SpentModel> get() = _newSpent

    init {
        getSpentTypes()
    }

    fun createSpent(amount: Double, typeId: String, note: String) {

        val spent = SpentModel(
            companyId = sessionManager.fetchUser()?.company?.id!!,
            typeId = typeId,
            note = note,
            cost = amount
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val spentUrl = "${sessionManager.fetchApiUrl()}/spent"
                val response = apiService.createSpent(spentUrl, spent)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("API_RESPONSE", "Created spent: ${_newSpent.value}")
                        getSpents();
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Create spent error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Exception creating spent: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error de red al crear gasto: ${e.message}")
                }
            }
        }
    }

    fun getSpents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val spentsUrl = "${sessionManager.fetchApiUrl()}/spent"
                val response = apiService.getSpents(spentsUrl)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _spents.value = response.body() ?: emptyList()
                        Log.d("API_RESPONSE", "Fetched spents: ${_spents.value?.size}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Fetch spents error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Exception fetching spents: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error de red al obtener gastos: ${e.message}")
                }
            }
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

    fun showToast(message: String) {
        _toastMessage.postValue(Event(message))
    }
}