package com.example.notasapp.presentation.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasapp.data.api.ApiClient
import com.example.notasapp.data.datastore.TokenStore
import com.example.notasapp.data.model.Note
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _currentOrder = MutableStateFlow<String?>(null)
    val currentOrder: StateFlow<String?> = _currentOrder

    private val _ascending = MutableStateFlow(true)
    val ascending: StateFlow<Boolean> = _ascending

    fun loadNotes(context: Context) {
        viewModelScope.launch {
            try {
                val token = TokenStore.getToken(context).first()
                val api = ApiClient.getApiService(token)
                val response = api.getNotes()

                if (response.isSuccessful) {
                    _notes.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Error al cargar notas: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            }
        }
    }

    fun updateSearch(query: String, context: Context) {
        _searchText.value = query

        if (query.isBlank()) {
            applyOrder(context)
        } else {
            viewModelScope.launch {
                try {
                    val token = TokenStore.getToken(context).first()
                    val api = ApiClient.getApiService(token)
                    val response = api.searchNotes(query)

                    if (response.isSuccessful) {
                        val result = response.body() ?: emptyList()
                        _notes.value = applyCurrentSorting(result)
                        _error.value = null
                    } else {
                        _error.value = "Error al buscar: ${response.code()}"
                    }
                } catch (e: Exception) {
                    _error.value = "Excepción: ${e.message}"
                }
            }
        }
    }

    fun toggleOrder(criteria: String, context: Context) {
        val ascendingNow = if (_currentOrder.value == criteria) !_ascending.value else true
        _currentOrder.value = criteria
        _ascending.value = ascendingNow

        applyOrder(context)
    }

    private fun applyOrder(context: Context) {
        val criteria = _currentOrder.value
        val query = _searchText.value

        viewModelScope.launch {
            try {
                val token = TokenStore.getToken(context).first()
                val api = ApiClient.getApiService(token)

                val notesList: List<Note> = if (query.isBlank()) {
                    val response = api.getNotes()
                    if (response.isSuccessful) {
                        response.body() ?: emptyList()
                    } else {
                        _error.value = "Error al ordenar: ${response.code()}"
                        return@launch
                    }
                } else {
                    val response = api.searchNotes(query)
                    if (response.isSuccessful) {
                        response.body() ?: emptyList()
                    } else {
                        _error.value = "Error al buscar: ${response.code()}"
                        return@launch
                    }
                }

                _notes.value = applyCurrentSorting(notesList)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            }
        }
    }

    private fun applyCurrentSorting(list: List<Note>): List<Note> {
        val order = _currentOrder.value
        val ascending = _ascending.value

        return when (order) {
            "title" -> {
                if (ascending) list.sortedBy { it.title.lowercase() }
                else list.sortedByDescending { it.title.lowercase() }
            }
            "created_at" -> {
                if (ascending) list.sortedBy { it.created_at }
                else list.sortedByDescending { it.created_at }
            }
            else -> list
        }
    }

    fun toggleFavorite(noteId: Int, context: Context) {
        viewModelScope.launch {
            try {
                val token = TokenStore.getToken(context).first()
                val response = ApiClient.getApiService(token).toggleFavorite("Bearer $token", noteId)

                if (response.isSuccessful) {
                    applyOrder(context)
                } else {
                    _error.value = "Error al cambiar favorito: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción: ${e.message}"
            }
        }
    }

    fun deleteNote(noteId: Int, context: Context) {
        viewModelScope.launch {
            TokenStore.getToken(context).collect { token ->
                if (!token.isNullOrEmpty()) {
                    val api = ApiClient.getApiService(token)
                    val res = api.deleteNote(noteId)
                    if (res.isSuccessful) {
                        _notes.value = _notes.value.filterNot { it.id == noteId }
                        _error.value = null
                    } else {
                        _error.value = "Error al eliminar nota"
                    }
                }
            }
        }
    }

}
