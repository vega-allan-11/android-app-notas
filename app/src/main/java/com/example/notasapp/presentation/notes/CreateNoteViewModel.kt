package com.example.notasapp.presentation.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasapp.data.api.ApiClient
import com.example.notasapp.data.datastore.TokenStore
import com.example.notasapp.data.model.NoteRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateNoteViewModel : ViewModel() {

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createNote(context: Context, title: String, content: String) {
        viewModelScope.launch {
            TokenStore.getToken(context).collect { token ->
                if (!token.isNullOrEmpty()) {
                    try {
                        val api = ApiClient.getApiService(token)
                        val response = api.createNote(NoteRequest(title, content))
                        if (response.isSuccessful) {
                            _success.value = true
                        } else {
                            _error.value = "Error: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        _error.value = "Excepción: ${e.message}"
                    }
                } else {
                    _error.value = "Token no disponible"
                }
            }
        }
    }
}
