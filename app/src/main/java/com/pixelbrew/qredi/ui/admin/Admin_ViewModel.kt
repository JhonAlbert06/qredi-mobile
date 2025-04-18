package com.pixelbrew.qredi.ui.admin


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.LoginRequest
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

                if (response.isSuccessful) {
                    val token = response.body()?.token.orEmpty()

                    sessionManager.saveAuthToken(token)
                    Log.d("API_RESPONSE", token)

                    loadUser() // Aquí se asume que esta función carga al usuario con el token
                    showToast("Login successful")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("API_RESPONSE", "Error: $errorBody")
                    showToast("Error: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                showToast("Error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private suspend fun loadUser() {
        try {
            val userUrl = "$baseUrl/user/loadUser"

            val response = withContext(Dispatchers.IO) {
                apiService.loadUser(userUrl)
            }

            if (response.isSuccessful) {
                val user = response.body()!!
                Log.d("API_RESPONSE", "User: $user")
                sessionManager.saveUser(user)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.d("API_RESPONSE", "Error: $errorBody")
                showToast("Error: $errorBody")
            }

        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
            showToast(e.message.toString())
        }
    }
}