package com.example.notasapp.data.repository

import com.example.notasapp.data.api.ApiClient
import com.example.notasapp.data.model.Note
import com.example.notasapp.data.model.NoteRequest
import retrofit2.Response

class NotesRepository {

    suspend fun getNotes(token: String): Response<List<Note>> {
        val api = ApiClient.getApiService(token)
        return api.getNotes()
    }

    suspend fun getNote(token: String, id: Int): Response<Note> {
        val api = ApiClient.getApiService(token)
        return api.getNote(id)
    }

    suspend fun createNote(token: String, request: NoteRequest): Response<Note> {
        val api = ApiClient.getApiService(token)
        return api.createNote(request)
    }

    suspend fun updateNote(token: String, id: Int, request: NoteRequest): Response<Note> {
        val api = ApiClient.getApiService(token)
        return api.updateNote(id, request)
    }

    suspend fun deleteNote(token: String, id: Int): Response<Unit> {
        val api = ApiClient.getApiService(token)
        return api.deleteNote(id)
    }

    suspend fun searchNotes(token: String, query: String): Response<List<Note>> {
        val api = ApiClient.getApiService(token)
        return api.searchNotes(query)
    }

    suspend fun orderNotes(token: String, orderBy: String, direction: String): Response<List<Note>> {
        val api = ApiClient.getApiService(token)
        return api.orderNotes(orderBy, direction)
    }



}
