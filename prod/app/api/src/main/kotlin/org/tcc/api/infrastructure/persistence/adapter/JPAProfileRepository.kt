package org.tcc.api.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import org.tcc.api.domain.profile.Profile
import org.tcc.api.domain.profile.ProfileRepository
import org.tcc.api.infrastructure.persistence.entity.ProfileEntity
import org.tcc.api.infrastructure.persistence.jpa.SpringDataProfileRepository
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

@Component
class JPAProfileRepository(
    private val springDataRepository: SpringDataProfileRepository
) : ProfileRepository {

    override fun save(profile: Profile): Profile {
        val id = profile.id

        val entity = ProfileEntity(
            id = id,
            name = profile.name,
            email = profile.email,
            passwordHash = profile.passwordHash,
        )

        val savedEntity = springDataRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun update(profile: Profile): Profile {
        val id = profile.id
        val entity = springDataRepository.findById(id).orElse(null)?.apply {
            this.name = profile.name
            this.email = profile.email
            this.passwordHash = profile.passwordHash
            this.updatedAt = Timestamp.valueOf(LocalDateTime.now())
        } ?: throw IllegalArgumentException("Profile with id ${profile.id} does not exist")

        val savedEntity = springDataRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(id: UUID): Profile? {
        return springDataRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByEmail(email: String): Profile? {
        return springDataRepository.findByEmail(email)?.toDomain()
    }

    override fun findAll(): List<Profile> {
        return springDataRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: UUID): Boolean {
        return if (springDataRepository.existsById(id)) {
            springDataRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    override fun existsByEmail(email: String): Boolean {
        return springDataRepository.existsByEmail(email)
    }

    private fun ProfileEntity.toDomain() = Profile(
        id = this.id,
        name = this.name,
        email = this.email,
        passwordHash = this.passwordHash,
        profilePicturePath = this.profilePicturePath,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}
