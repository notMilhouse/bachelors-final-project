package org.tcc.api.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import org.tcc.api.domain.profile.Profile
import org.tcc.api.domain.profile.ProfileRepository
import org.tcc.api.infrastructure.persistence.entity.ProfileJPAEntity
import org.tcc.api.infrastructure.persistence.jpa.SpringDataProfileRepository
import java.util.UUID

@Component
class JPAProfileRepository(
    private val springDataRepository: SpringDataProfileRepository
) : ProfileRepository {

    // In JPAProfileRepository
    override fun save(profile: Profile): Profile {
        val entity = if (profile.id == null) {
            // Definitely new
            createNewEntity(profile)
        } else {
            // Check if exists
            springDataRepository.findById(profile.id!!).orElse(null)?.let {
                // Exists - update it
                updateExistingEntity(it, profile)
            } ?: run {
                // Doesn't exist - create new with this ID
                createNewEntity(profile)
            }
        }

        val savedEntity = springDataRepository.save(entity)
        return savedEntity.toDomain()
    }

    private fun createNewEntity(profile: Profile): ProfileJPAEntity {
        return ProfileJPAEntity(
            id = null,
            name = profile.name
        )
    }

    private fun updateExistingEntity(entity: ProfileJPAEntity, profile: Profile): ProfileJPAEntity {
        return entity.apply {
            this.name = profile.name
        }
    }

    override fun findById(id: UUID): Profile? {
        return springDataRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findAll(): List<Profile> {
        return springDataRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: UUID): Boolean {
        if (springDataRepository.existsById(id)) {
            springDataRepository.deleteById(id)
            return true
        }

        return false
    }

    private fun ProfileJPAEntity.toDomain() = Profile(
        id = this.id,
        name = this.name,
    )
}
