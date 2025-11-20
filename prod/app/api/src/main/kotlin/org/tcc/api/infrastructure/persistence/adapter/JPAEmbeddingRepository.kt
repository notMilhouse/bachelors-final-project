package org.tcc.api.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import org.tcc.api.domain.embedding.Embedding
import org.tcc.api.domain.embedding.EmbeddingRepository
import org.tcc.api.infrastructure.persistence.entity.EmbeddingJPAEntity
import org.tcc.api.infrastructure.persistence.jpa.SpringDataEmbeddingRepository
import org.tcc.api.infrastructure.persistence.jpa.SpringDataProfileRepository
import java.util.UUID

@Component
class JPAEmbeddingRepository (
    private val springDataRepository: SpringDataEmbeddingRepository,
    private val profileRepository: SpringDataProfileRepository
) : EmbeddingRepository {

    override fun save(embedding: Embedding): Embedding {
        // Find the profile entity
        val profileEntity = embedding.profileId?.let { profileId ->
            profileRepository.findById(profileId)
                .orElseThrow { IllegalArgumentException("Profile not found with id: $profileId") }
        } ?: throw IllegalArgumentException("Profile ID is required")

        // Check if embedding already exists, otherwise create new
        val entity = if (embedding.id != null) {
            // Update existing
            springDataRepository.findById(embedding.id!!).orElse(null)?.apply {
                this.embedding = embedding.embeddingVector
                this.profile = profileEntity
            } ?: throw IllegalArgumentException("Embedding not found with id: ${embedding.id}")
        } else {
            // Create new
            EmbeddingJPAEntity(
                id = null,
                embedding = embedding.embeddingVector,
                profile = profileEntity
            )
        }

        val savedEntity = springDataRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(id: UUID): Embedding? {
        return springDataRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByProfileId(profileId: UUID): List<Embedding> {
        return springDataRepository.findByProfileId(profileId)
            .map { it.toDomain() }
    }

    override fun findAll(): List<Embedding> {
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

    private fun EmbeddingJPAEntity.toDomain() = Embedding(
        id = this.id,
        embeddingVector = this.embedding,
        profileId = this.profile.id,
    )
}