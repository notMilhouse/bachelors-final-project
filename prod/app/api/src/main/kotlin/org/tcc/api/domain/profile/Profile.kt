package org.tcc.api.domain.profile

import java.sql.Timestamp
import java.util.UUID

data class Profile(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val email: String,
    val passwordHash: String,
    val profilePicturePath: String?,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
) {

    fun updateDetails(name: String, email: String): Profile {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(name.length <= 100) { "Name cannot exceed 100 characters" }

        return copy(
            name = name,
            email = email
        )
    }

    fun changePassword(newPassword: String): Profile {
        return copy(passwordHash = newPassword)
    }
}
