package org.tcc.api.application.profile.dto

import java.util.UUID

data class ProfileResponse(
    val id: UUID? = null,
    val name: String
)
