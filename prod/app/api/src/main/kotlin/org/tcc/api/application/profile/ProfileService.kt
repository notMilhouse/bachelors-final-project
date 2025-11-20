package org.tcc.api.application.profile

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tcc.api.application.profile.dto.CreateProfileRequest
import org.tcc.api.application.profile.dto.ProfileResponse
import org.tcc.api.application.profile.dto.UpdateProfileRequest
import org.tcc.api.domain.profile.Profile
import org.tcc.api.domain.profile.ProfileRepository
import java.util.UUID

@Service
@Transactional
class ProfileService(
    private val profileRepository: ProfileRepository
) {

    fun createProfile(request: CreateProfileRequest): ProfileResponse {
        val profile = Profile(
            id = null,
            name = request.name
        )

        val savedProfile = profileRepository.save(profile)
        return savedProfile.toResponse()
    }

    @Transactional(readOnly = true)
    fun getProfileById(id: UUID): ProfileResponse? {
        return profileRepository.findById(id)?.toResponse()
    }

    @Transactional(readOnly = true)
    fun getAllProfiles(): List<ProfileResponse> {
        return profileRepository.findAll().map { it.toResponse() }
    }

    fun updateProfile(id: UUID, request: UpdateProfileRequest): ProfileResponse? {
        val existingProfile = profileRepository.findById(id) ?: return null

        val updatedProfile = existingProfile.updateDetails(
            name = request.name,
        )

        val savedProfile = profileRepository.save(updatedProfile)
        return savedProfile.toResponse()
    }

    fun deleteProfile(id: UUID): Boolean {
        return profileRepository.deleteById(id)
    }

    private fun Profile.toResponse() = ProfileResponse(
        id = this.id,
        name = this.name
    )
}