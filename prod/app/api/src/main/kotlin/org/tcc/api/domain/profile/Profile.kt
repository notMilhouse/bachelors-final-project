package org.tcc.api.domain.profile

import java.util.UUID

data class Profile(
    var id: UUID? = null,
    val name: String,
) {

    fun updateDetails(name: String): Profile {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(name.length <= 100) { "Name cannot exceed 100 characters" }

        return copy(
            name = name
        )
    }
}
