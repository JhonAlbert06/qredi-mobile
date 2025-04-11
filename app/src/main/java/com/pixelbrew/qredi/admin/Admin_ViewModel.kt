package com.pixelbrew.qredi.admin


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.LoginRequest
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val baseUrl = sessionManager.fetchApiUrl()

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>("")
    val password: LiveData<String> = _password

    private val _isLoginEnabled = MutableLiveData<Boolean>(false)
    val isLoginEnabled: LiveData<Boolean> = _isLoginEnabled

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun onLoginChange(email: String, password: String) {
        _email.value = email
        _password.value = password

        _isLoginEnabled.value = isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun isValidEmail(username: String): Boolean {
        return username.isNotEmpty()
    }

    private fun showToast(message: String) {
        _toastMessage.postValue(message)
    }

    fun onLoginSelected() {
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(_email.value.toString(), _password.value.toString())

                Log.d(
                    "API_REQUEST",
                    "Email: ${loginRequest.username}, Password: ${loginRequest.password}"
                )

                val loginUrl = "$baseUrl/user/login"

                val response = withContext(Dispatchers.IO) {
                    apiService.login(loginUrl, loginRequest)
                }

                sessionManager.saveAuthToken(response.token)
                Log.d("API_RESPONSE", response.token)

                loadUser()

                _isLoading.postValue(false)
                showToast("Login successful")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                _isLoading.postValue(false)
                showToast(e.message.toString())
            }
        }
    }

    private suspend fun loadUser() {
        try {
            val userUrl = "$baseUrl/user/loadUser"

            val response = withContext(Dispatchers.IO) {
                apiService.loadUser(userUrl)
            }

            sessionManager.saveUser(response)
            Log.d("API_RESPONSE", response.toString())
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
            showToast(e.message.toString())
        }
    }
}