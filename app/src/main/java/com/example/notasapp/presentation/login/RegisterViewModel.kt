package com.example.notasapp.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasapp.data.api.ApiClient
import com.example.notasapp.data.datastore.TokenStore
import com.example.notasapp.data.model.LoginRequest
import com.example.notasapp.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class RegisterViewModel : ViewModel() {

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun register(context: Context, name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                val api = ApiClient.getApiService()

                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    password_confirmation = confirmPassword
                )

                val response = api.register(request)

                if (response.isSuccessful) {
                    // Login automático
                    val loginResponse = api.login(LoginRequest(email, password))

                    if (loginResponse.isSuccessful) {
                        val token = loginResponse.body()?.access_token
                        if (!token.isNullOrEmpty()) {
                            TokenStore.saveToken(context, token)
                            _success.value = true
                        } else {
                            _error.value = "Token no recibido después del login"
                        }
                    } else {
                        _error.value = "Registro exitoso, pero login falló"
                    }
                } else {
                    // Extraer mensaje de error detallado del cuerpo
                    val errorBody = response.errorBody()?.string()
                    val json = errorBody?.let { JSONObject(it) }

                    if (json != null && json.has("errors")) {
                        val errors = json.getJSONObject("errors")
                        val messages = mutableListOf<String>()

                        for (key in errors.keys()) {
                            val fieldErrors = errors.getJSONArray(key)
                            for (i in 0 until fieldErrors.length()) {
                                messages.add(fieldErrors.getString(i))
                            }
                        }

                        _error.value = messages.joinToString("\n")
                    } else {
                        _error.value = "Error de registro: ${response.code()}"
                    }
                }
            } catch (e: HttpException) {
                _error.value = "Error HTTP: ${e.message}"
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            }
        }
    }
}
