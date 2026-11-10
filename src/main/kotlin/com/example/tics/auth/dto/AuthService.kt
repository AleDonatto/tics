package com.example.tics.auth.dto

import com.example.tics.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder
) {
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByUserName(request.username) ?: throw invalidCredentials()

        if(!user.userActive){
            throw desactiveUser()
        } else if(!passwordEncoder.matches(
            request.password,
            user.userPassword
        )) {
            throw invalidCredentials()
        }

        val role = user.rol?.nameRol ?: throw ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "User needs to have a role."
        )

        val now = Instant.now()
        val expiresAt = now.plusSeconds(3600)
        val claims = JwtClaimsSet.builder()
            .issuer("jfa-tics")
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.uuid)
            .claim("username", request.username)
            .claim("role", role)
            .build()

        val header = JwsHeader.with(MacAlgorithm.HS256).build()

        val token = jwtEncoder.encode(JwtEncoderParameters.from(header,claims)).tokenValue

        return LoginResponse(
            accessToken = token,
            expiresIn = 3600,
            user = AuthenticatedUser(
                uuid = user.uuid,
                username = user.userName,
                nombre = user.nombre,
                grado = user.grado,
                active = user.userActive,
                rol = role
            )
        )
    }

    private fun invalidCredentials() = ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Invalid username or password."
    )

    private fun desactiveUser() = ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Inactive user."
    )
}