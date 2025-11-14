package org.tcc.api.application.profile.dto

data class CreateProfileRequest(
    val name: String,
    val email: String,
    val password: String
)
