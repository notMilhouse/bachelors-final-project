package org.tcc.api.infrastructure.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tcc.api.application.profile.ProfileService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.tcc.api.application.profile.dto.ChangePasswordRequest
import org.tcc.api.application.profile.dto.CreateProfileRequest
import org.tcc.api.application.profile.dto.ProfileResponse
import org.tcc.api.application.profile.dto.UpdateProfileRequest
import java.util.UUID

@RestController
@RequestMapping("/api/profiles")
class ProfileController(
    private val profileService: ProfileService
) {

    @PostMapping
    fun createProfile(@RequestBody request: CreateProfileRequest): ResponseEntity<ProfileResponse> {
        return try {
            val profile = profileService.createProfile(request)
            ResponseEntity.status(HttpStatus.CREATED).body(profile)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{id}")
    fun getProfileById(@PathVariable id: UUID): ResponseEntity<ProfileResponse> {
        val profile = profileService.getProfileById(id)
        return profile?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAllProfiles(): ResponseEntity<List<ProfileResponse>> {
        val profiles = profileService.getAllProfiles()
        return ResponseEntity.ok(profiles)
    }

    @GetMapping("/by-email")
    fun getProfileByEmail(@RequestParam email: String): ResponseEntity<ProfileResponse> {
        val profile = profileService.getProfileByEmail(email)
        return profile?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    fun updateProfile(
        @PathVariable id: UUID,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<ProfileResponse> {
        return try {
            val profile = profileService.updateProfile(id, request)
            profile?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PatchMapping("/{id}/password")
    fun changePassword(
        @PathVariable id: UUID,
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ProfileResponse> {
        return try {
            val profile = profileService.changePassword(id, request)
            profile?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProfile(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (profileService.deleteProfile(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}