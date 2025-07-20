package com.example.notasapp.presentation.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasapp.data.api.ApiClient
import com.example.notasapp.data.datastore.TokenStore
import com.example.notasapp.data.model.Note
import com.example.notasapp.data.model.NoteRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteDetailViewModel : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note

    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted

    private val _updated = MutableStateFlow(false)
    val updated: StateFlow<Boolean> = _updated

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadNote(context: Context, id: Int) {
        viewModelScope.launch {
            TokenStore.getToken(context).collect { token ->
                if (!token.isNullOrEmpty()) {
                    val api = ApiClient.getApiService(token)
                    val res = api.getNote(id)
                    if (res.isSuccessful) {
                        _note.value = res.body()
                    } else {
                        _error.value = "No se pudo cargar la nota"
                    }
                }
            }
        }
    }

    fun updateNote(context: Context, id: Int, title: String, content: String) {
        viewModelScope.launch {
            TokenStore.getToken(context).collect { token ->
                if (!token.isNullOrEmpty()) {
                    val api = ApiClient.getApiService(token)
                    val res = api.updateNote(id, NoteRequest(title, content))
                    if (res.isSuccessful) {
                        _updated.value = true
                    } else {
                        _error.value = "Error al actualizar"
                    }
                }
            }
        }
    }

    fun deleteNote(context: Context, id: Int) {
        viewModelScope.launch {
            TokenStore.getToken(context).collect { token ->
                if (!token.isNullOrEmpty()) {
                    val api = ApiClient.getApiService(token)
                    val res = api.deleteNote(id)
                    if (res.isSuccessful) {
                        _deleted.value = true
                    } else {
                        _error.value = "Error al eliminar"
                    }
                }
            }
        }
    }
}
