package com.example.tics.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "roles")
class RolModel {
    @Id
    @Column(
        name = "idroles",
        nullable = false,
        unique = true,
        updatable = false,
        length = 36,
    )
    var uuid: String = UUID.randomUUID().toString()
    @Column(name= "name_rol" , nullable = false)
    var nameRol: String = ""
}