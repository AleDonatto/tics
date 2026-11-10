package com.example.tics.servicies

import com.example.tics.dto.RolesResponseDto
import com.example.tics.dto.UserChangeEstatusDto
import com.example.tics.dto.UsuariosCreateDto
import com.example.tics.dto.UsuariosResponseDto
import com.example.tics.dto.UsuariosUpdateDto
import com.example.tics.models.RolModel
import com.example.tics.models.UserModel
import com.example.tics.repositories.RolesRepository
import com.example.tics.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val rolesRepository: RolesRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private fun toResponse(user: UserModel): UsuariosResponseDto {
        val rol = user.rol ?: throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "user can't be rol"
        )

        return UsuariosResponseDto(
            uuid = user.uuid,
            grado = user.grado,
            nombre = user.nombre,
            username = user.userName,
            active = user.userActive,
            rolId = rol.uuid,
            role = rol.nameRol.orEmpty()
        )
    }

    @Transactional
    fun findAll(): List<UsuariosResponseDto> =
        userRepository.findAll().map(::toResponse)

    @Transactional
    fun getAllRoles(): List<RolesResponseDto> = rolesRepository.findAll().map {
        rol ->
        RolesResponseDto(
            uuid = rol.uuid,
            nameRol = rol.nameRol.orEmpty(),
        )
    }

    @Transactional
    fun findById(uuid: String): UsuariosResponseDto =
        toResponse(getUser(uuid))

    @Transactional
    fun me(authentication: Authentication): UsuariosResponseDto =
        toResponse(getUser(authentication.name))

    @Transactional
    fun create(request: UsuariosCreateDto): UsuariosResponseDto {
        if (userRepository.existsByUserName(request.username)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "El nombre de usuario ya existe"
            )
        }

        val rol = getRol(request.rolId)

        val user = UserModel().apply {
            grado = request.grado
            nombre = request.nombre
            userName = request.username
            userPassword = passwordEncoder.encode(request.password) ?: ""
            userActive = true
            this.rol = rol
        }

        return toResponse(userRepository.save(user))
    }

    @Transactional
    fun update(
        uuid: String,
        request: UsuariosUpdateDto
    ): UsuariosResponseDto {
        val user = getUser(uuid)

        if (userRepository.existsByUserNameAndUuidNot(
                request.username, uuid
            )
        ) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "El nombre de usuario ya existe"
            )
        }

        user.grado = request.grado
        user.nombre = request.nombre
        user.userName = request.username
        user.rol = getRol(request.rol)

        return toResponse(userRepository.save(user))
    }

    @Transactional
    fun updateStatus(
        uuid: String,
        request: UserChangeEstatusDto,
        authentication: Authentication
    ): UsuariosResponseDto {
        if (uuid == authentication.name && !request.active) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No puedes desactivar tu propia cuenta"
            )
        }

        val user = getUser(uuid)
        user.userActive = request.active

        return toResponse(userRepository.save(user))
    }

    private fun getUser(uuid: String): UserModel =
        userRepository.findById(uuid).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Usuario no encontrado"
            )
        }

    private fun getRol(uuid: String): RolModel =
        rolesRepository.findById(uuid).orElseThrow {
            ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El rol seleccionado no existe"
            )
        }
}