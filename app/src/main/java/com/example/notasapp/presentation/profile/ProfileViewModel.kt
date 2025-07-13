package com.example.notasapp.presentation.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasapp.data.api.ApiClient
import com.example.notasapp.data.datastore.TokenStore
import com.example.notasapp.data.model.UpdateUserRequest
import com.example.notasapp.data.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    val user = mutableStateOf<User?>(null)

    fun loadProfile(context: Context) {
        viewModelScope.launch {
            try {
                val token = TokenStore.getToken(context).first()
                val response = ApiClient.getApiService(token).getProfile("Bearer $token")
                user.value = response
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar perfil", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateProfile(
        context: Context,
        name: String,
        email: String,
        currentPassword: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            try {
                val token = TokenStore.getToken(context).first()

                val request = UpdateUserRequest(
                    name = if (name.isNotBlank()) name else null,
                    email = if (email.isNotBlank()) email else null,
                    current_password = if (currentPassword.isNotBlank()) currentPassword else null,
                    password = if (password.isNotBlank()) password else null,
                    password_confirmation = if (confirmPassword.isNotBlank()) confirmPassword else null
                )

                val response = ApiClient.getApiService(token).updateProfile(
                    token = "Bearer $token",
                    request = request
                )

                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun logout(context: Context, onLogout: () -> Unit) {
        viewModelScope.launch {
            TokenStore.clearToken(context)
            onLogout()
        }
    }
}
