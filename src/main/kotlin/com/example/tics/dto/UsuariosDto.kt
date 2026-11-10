package com.example.tics.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UsuariosCreateDto(
    @field:NotBlank
    val grado : String,

    @field:NotBlank
    val nombre : String,

    @field:NotBlank
    val username : String,

    @field:Size(min = 8, max = 72)
    val password : String,

    @field:NotBlank
    val rolId: String
)

data class UsuariosUpdateDto(
    @field:NotBlank
    val grado : String,

    @field:NotBlank
    val nombre : String,

    @field:NotBlank
    val username : String,

    @field:NotBlank
    val rol : String,
)

data class UserChangeEstatusDto(
    val active: Boolean
)

data class UsuariosResponseDto(
    val uuid : String,
    val grado : String,
    val nombre : String,
    val username : String,
    val active : Boolean,
    val rolId : String,
    val role : String,
)

data class RolesResponseDto(
    val uuid : String,
    val nameRol : String,
)