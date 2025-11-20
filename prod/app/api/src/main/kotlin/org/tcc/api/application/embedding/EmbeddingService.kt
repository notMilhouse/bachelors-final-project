package org.tcc.api.application.embedding

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tcc.api.application.embedding.dto.CreateEmbeddingRequest
import org.tcc.api.application.embedding.dto.EmbeddingResponse
import org.tcc.api.domain.embedding.Embedding
import org.tcc.api.domain.embedding.EmbeddingRepository
import org.tcc.api.domain.profile.ProfileRepository
import java.util.UUID

@Service
@Transactional
class EmbeddingService(
    private val embeddingRepository: EmbeddingRepository,
    private val profileRepository: ProfileRepository,
    private val extractEmbeddings: ExtractEmbeddings
) {

    fun createEmbedding(request: CreateEmbeddingRequest): EmbeddingResponse {
        val profileId = request.profileId

        profileRepository.findById(profileId)
            ?: throw IllegalArgumentException("Profile not found")

        val embedding = Embedding(
            profileId = profileId,
            embeddingVector = extractEmbeddings.retrieveEmbeddingVectorFromRequestImage(request.image),
        )

        val savedEmbedding = embeddingRepository.save(embedding)
        return savedEmbedding.toResponse()
    }

    @Transactional(readOnly = true)
    fun getEmbeddingById(id: UUID): EmbeddingResponse? {
        return embeddingRepository.findById(id)?.toResponse()
    }

    @Transactional(readOnly = true)
    fun getEmbeddingsByProfileId(profileId: UUID): List<EmbeddingResponse> {
        return embeddingRepository.findByProfileId(profileId)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getAllEmbeddings(): List<EmbeddingResponse> {
        return embeddingRepository.findAll().map { it.toResponse() }
    }


    fun deleteById(id: UUID): Boolean {
        return embeddingRepository.deleteById(id)
    }

    private fun Embedding.toResponse() = EmbeddingResponse(
        id = this.id,
        embeddingVector = this.embeddingVector,
        profileId = this.profileId
    )
}