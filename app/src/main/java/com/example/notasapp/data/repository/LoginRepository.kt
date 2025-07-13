package com.example.notasapp.data.repository

import com.example.notasapp.data.api.ApiClient
import com.example.notasapp.data.model.LoginRequest
import com.example.notasapp.data.model.TokenResponse
import retrofit2.Response

class LoginRepository {
    private val api = ApiClient.getApiService()

    suspend fun login(request: LoginRequest): Response<TokenResponse> {
        return api.login(request)
    }
}
