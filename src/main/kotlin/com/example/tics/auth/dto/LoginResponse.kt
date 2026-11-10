package com.example.tics.auth.dto

data class LoginResponse (
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: AuthenticatedUser,
)

data class AuthenticatedUser(
    val uuid: String,
    val username: String,
    val nombre : String,
    val grado : String,
    val active: Boolean,
    val rol : String,
)