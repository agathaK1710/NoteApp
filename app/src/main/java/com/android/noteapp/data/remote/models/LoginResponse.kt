package com.android.noteapp.data.remote.models

data class LoginResponse(
    val role: Role,
    val success: Boolean,
    val message: String
)