package org.tcc.api.domain.embedding

import java.util.UUID

interface EmbeddingRepository {
    fun save(embedding: Embedding): Embedding
    fun findById(id: UUID): Embedding?
    fun findByProfileId(profileId: UUID): List<Embedding>
    fun findAll(): List<Embedding>
    fun deleteById(id: UUID): Boolean
    fun findProfileIdByDistance(featureVector: DoubleArray): UUID?
}
