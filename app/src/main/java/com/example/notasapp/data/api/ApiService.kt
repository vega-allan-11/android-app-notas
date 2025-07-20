package com.example.notasapp.data.api

import com.example.notasapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiMessage>

    @POST("logout")
    suspend fun logout(): Response<ApiMessage>

    // Notas
    @GET("notes")
    suspend fun getNotes(): Response<List<Note>>

    @POST("notes")
    suspend fun createNote(@Body request: NoteRequest): Response<Note>

    @GET("notes/{id}")
    suspend fun getNote(@Path("id") id: Int): Response<Note>

    @PUT("notes/{id}")
    suspend fun updateNote(@Path("id") id: Int, @Body request: NoteRequest): Response<Note>

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Int): Response<Unit>

    @GET("notes/search/{text}")
    suspend fun searchNotes(@Path("text") query: String): Response<List<Note>>

    @GET("notes/order/{by}")
    suspend fun orderNotes(
        @Path("by") criteria: String,
        @Query("direction") direction: String
    ): Response<List<Note>>

    @PATCH("notes/{id}/favorite")
    suspend fun toggleFavorite(
        @Header("Authorization") token: String,
        @Path("id") noteId: Int
    ): Response<Note>

    // Perfil
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateUserRequest
    ): ApiMessage

    @GET("user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): User
}
