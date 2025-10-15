package org.tcc.api.domain.profile

import java.util.UUID

data class Profile(
    val name: String,
    val id: UUID,
    val email: String,
    val password: String,
)
