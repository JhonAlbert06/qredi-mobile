package com.pixelbrew.qredi.ui.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.ApiError
import com.pixelbrew.qredi.data.network.model.LoginRequest
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val baseUrl = sessionManager.fetchApiUrl()

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>("")
    val password: LiveData<String> = _password

    private val _isLoginEnabled = MutableLiveData<Boolean>(false)
    val isLoginEnabled: LiveData<Boolean> = _isLoginEnabled

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private fun showToast(message: String) {
        _toastMessage.postValue(Event(message))
    }

    fun onLoginChange(email: String, password: String) {
        _email.value = email
        _password.value = password
        _isLoginEnabled.value = isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidPassword(password: String) = password.length >= 6
    private fun isValidEmail(email: String) = email.isNotBlank()

    fun onLoginSelected() {
        _isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(_email.value.orEmpty(), _password.value.orEmpty())
                Log.d("API_REQUEST", "Login attempt: ${loginRequest.username}")

                val loginUrl = "$baseUrl/user/login"
                val response = apiService.login(loginUrl, loginRequest)

                if (response.isSuccessful) {
                    val token = response.body()?.token.orEmpty()
                    sessionManager.saveAuthToken(token)
                    Log.d("API_RESPONSE", "Token received: $token")

                    loadUser()  // Carga el usuario y lo guarda
                    withContext(Dispatchers.Main) {
                        showToast("Login exitoso")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = Gson().fromJson(errorBody, ApiError::class.java)
                    Log.e("API_RESPONSE", "Login failed: ${error.message}")
                    withContext(Dispatchers.Main) {
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Login error: ${e.message}")
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) { _isLoading.value = false }
            }
        }
    }

    private suspend fun loadUser() {
        try {
            val userUrl = "$baseUrl/user/loadUser"
            val response = apiService.loadUser(userUrl)

            if (response.isSuccessful) {
                val user = response.body()
                Log.d("API_RESPONSE", "User loaded: $user")
                user?.let { sessionManager.saveUser(it) }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("API_RESPONSE", "LoadUser failed: $errorBody")
                withContext(Dispatchers.Main) { showToast("Error: $errorBody") }
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error loading user: ${e.message}")
            withContext(Dispatchers.Main) { showToast("Error: ${e.message}") }
        }
    }
}
