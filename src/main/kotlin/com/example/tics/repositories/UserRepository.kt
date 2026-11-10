package com.example.tics.repositories

import com.example.tics.models.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserModel, String> {
    fun findByUserName(userName: String): UserModel?

    fun existsByUserName(userName: String): Boolean

    fun existsByUserNameAndUuidNot(
        userName: String,
        uuid: String
    ): Boolean
}