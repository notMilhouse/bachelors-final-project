package org.tcc.api.application.profile

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tcc.api.application.profile.dto.ChangePasswordRequest
import org.tcc.api.application.profile.dto.CreateProfileRequest
import org.tcc.api.application.profile.dto.ProfileResponse
import org.tcc.api.application.profile.dto.UpdateProfileRequest
import org.tcc.api.domain.profile.Profile
import org.tcc.api.domain.profile.ProfileRepository
import java.sql.Timestamp
import java.util.UUID

@Service
@Transactional
class ProfileService(
    private val profileRepository: ProfileRepository
) {

    fun createProfile(request: CreateProfileRequest): ProfileResponse {
        if (profileRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val currentTimestamp = Timestamp(System.currentTimeMillis())

        val profile = Profile(
            name = request.name,
            email = request.email,
            passwordHash = request.password,
            id = UUID.randomUUID(),
            profilePicturePath = "",
            createdAt = currentTimestamp,
            updatedAt = currentTimestamp
        )

        val savedProfile = profileRepository.save(profile)
        return savedProfile.toResponse()
    }

    @Transactional(readOnly = true)
    fun getProfileById(id: UUID): ProfileResponse? {
        return profileRepository.findById(id)?.toResponse()
    }

    @Transactional(readOnly = true)
    fun getProfileByEmail(email: String): ProfileResponse? {
        return profileRepository.findByEmail(email)?.toResponse()
    }

    @Transactional(readOnly = true)
    fun getAllProfiles(): List<ProfileResponse> {
        return profileRepository.findAll().map { it.toResponse() }
    }

    fun updateProfile(id: UUID, request: UpdateProfileRequest): ProfileResponse? {
        val profileId = id
        val existingProfile = profileRepository.findById(profileId) ?: return null

        if (request.email != existingProfile.email &&
            profileRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val updatedProfile = existingProfile.updateDetails(
            name = request.name,
            email = request.email
        )

        val savedProfile = profileRepository.update(updatedProfile)
        return savedProfile.toResponse()
    }

    fun changePassword(id: UUID, request: ChangePasswordRequest): ProfileResponse {
        val profileId = id
        val existingProfile = profileRepository.findById(profileId) ?: return ProfileResponse(
            id = UUID.fromString("Profile Not Found"),
            name = "Profile Not Found",
            email = "Profile Not Found",
        )

        val updatedProfile = existingProfile.changePassword(request.newPassword)
        val savedProfile = profileRepository.save(updatedProfile)
        return savedProfile.toResponse()
    }

    fun deleteProfile(id: UUID): Boolean {
        return profileRepository.deleteById(id)
    }

    private fun Profile.toResponse() = ProfileResponse(
        id = this.id,
        name = this.name,
        email = this.email
    )
}