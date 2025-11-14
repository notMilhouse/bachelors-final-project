package org.tcc.api.application.profile.dto

import java.util.UUID

data class ProfileResponse(
    val id: UUID,
    val name: String,
    val email: String
)
