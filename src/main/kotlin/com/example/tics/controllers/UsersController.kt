package com.example.tics.controllers

import com.example.tics.dto.RolesResponseDto
import com.example.tics.dto.UserChangeEstatusDto
import com.example.tics.dto.UsuariosCreateDto
import com.example.tics.dto.UsuariosResponseDto
import com.example.tics.dto.UsuariosUpdateDto
import com.example.tics.servicies.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UsersController(private val usuariosService: UserService) {

    @GetMapping("/me")
    fun me(authentication: Authentication): UsuariosResponseDto = usuariosService.me(authentication)

    @GetMapping("/all")
    fun findAll(): List<UsuariosResponseDto> = usuariosService.findAll()

    @GetMapping("/{uuid}")
    fun findById(
        @PathVariable uuid: String
    ): UsuariosResponseDto = usuariosService.findById(uuid)

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @Valid @RequestBody user: UsuariosCreateDto
    ): UsuariosResponseDto = usuariosService.create(user)

    @PutMapping("/{uuid}/status")
    fun updateStatus(
        @PathVariable uuid: String,
        @RequestBody user: UserChangeEstatusDto,
        authentication: Authentication
    ): UsuariosResponseDto = usuariosService.updateStatus(uuid, user, authentication)

    @PutMapping("/{uuid}")
    fun update(
        @PathVariable uuid: String,
        @Valid @RequestBody request: UsuariosUpdateDto
    ): UsuariosResponseDto =
        usuariosService.update(uuid, request)

    @GetMapping("/roles")
    fun getAllRoles() : List<RolesResponseDto> = usuariosService.getAllRoles()
}