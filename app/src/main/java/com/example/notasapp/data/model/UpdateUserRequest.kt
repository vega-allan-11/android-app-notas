package com.example.notasapp.data.model

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val current_password: String? = null,
    val password: String? = null,
    val password_confirmation: String? = null
)

