package com.example.tics.repositories

import com.example.tics.models.RolModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface RolesRepository: JpaRepository<RolModel, String> {
    fun findByNameRol(role: String): RolModel?
}