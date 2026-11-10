package com.example.tics.auth.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest (
    @field:_root_ide_package_.jakarta.validation.constraints.NotBlank
    val username: String,

    @field:_root_ide_package_.jakarta.validation.constraints.NotBlank
    val password: String,
)