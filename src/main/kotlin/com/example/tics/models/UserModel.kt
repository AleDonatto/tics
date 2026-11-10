package com.example.tics.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "usuarios")
class UserModel {

    @Id
    @Column(
        name = "iduser",
                nullable = false,
                unique = true,
                updatable = false,
                length = 36,
            )
    var uuid: String = UUID.randomUUID().toString()
    @Column(name = "grado",nullable = false)
    var grado: String = ""
    @Column(name="nombre", nullable = false)
    var nombre: String = ""
    @Column(name="username", nullable = false)
    var userName: String = ""
    @Column(name="user_password", nullable = false)
    var userPassword: String = ""
    @Column(name="user_active", nullable = false)
    var userActive : Boolean = false
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    var rol : RolModel? = null

}