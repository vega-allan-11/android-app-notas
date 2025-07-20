package com.example.notasapp.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasapp.data.datastore.TokenStore
import com.example.notasapp.data.model.LoginRequest
import com.example.notasapp.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = repository.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val token = response.body()?.access_token
                    if (!token.isNullOrEmpty()) {
                        TokenStore.saveToken(context, token)
                        _loginSuccess.value = true
                    } else {
                        _error.value = "Token no recibido"
                    }
                } else {
                    _error.value = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            }
        }
    }
}
