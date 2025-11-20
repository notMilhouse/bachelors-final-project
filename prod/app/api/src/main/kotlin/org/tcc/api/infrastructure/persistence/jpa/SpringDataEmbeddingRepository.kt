package org.tcc.api.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tcc.api.infrastructure.persistence.entity.EmbeddingJPAEntity
import java.util.UUID


@Repository
interface SpringDataEmbeddingRepository : JpaRepository<EmbeddingJPAEntity, UUID> {
    fun findByProfileId(profileId: UUID): List<EmbeddingJPAEntity>
}
