package com.example.tics.config

import com.example.tics.models.RolModel
import com.example.tics.models.UserModel
import com.example.tics.repositories.RolesRepository
import com.example.tics.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AdminSeeder(
    private val userRepository: UserRepository,
    private val rolRepository: RolesRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Bean
    fun seedAdmin() = ApplicationRunner {
        seed()
    }

    @Transactional
    fun seed() {
        val role = rolRepository.findById( rolRepository.findByNameRol("admin")?.uuid ?: "" ).orElseGet {
            var rolModel = RolModel()
            rolModel.nameRol = "admin"
            rolRepository.save(rolModel)
        }

        if(userRepository.existsByUserName( "administrator") ) {
            return
        }

        val userAdmin = UserModel()
        userAdmin.grado = "Soldado"
        userAdmin.nombre = "Alejandro Donato"
        userAdmin.userName = "administrator"
        userAdmin.userPassword = passwordEncoder.encode("admin123") ?: ""
        userAdmin.userActive = true
        userAdmin.rol = role

        userRepository.save(userAdmin)
    }

}