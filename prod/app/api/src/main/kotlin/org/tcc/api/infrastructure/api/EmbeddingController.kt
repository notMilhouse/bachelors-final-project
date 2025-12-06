package org.tcc.api.infrastructure.api

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.tcc.api.application.embedding.EmbeddingService
import org.tcc.api.application.embedding.dto.CreateEmbeddingRequest
import org.tcc.api.application.embedding.dto.EmbeddingResponse
import org.tcc.api.application.embedding.dto.SearchEmbeddingRequest
import org.tcc.api.domain.embedding.Embedding
import java.util.UUID

@RestController
@RequestMapping("/api/embeddings")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class EmbeddingController(
    private val embeddingService: EmbeddingService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createEmbedding(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<EmbeddingResponse> {
        return try {
//            val request = CreateEmbeddingRequest(
//                image = file,
//                profileId = UUID.fromString(profileId)
//            )
//            val embedding = embeddingService.createEmbedding(request)
            ResponseEntity.status(HttpStatus.CREATED).body(EmbeddingResponse(
                id = UUID.randomUUID(),
                profileId = UUID.randomUUID(),
                featureVector = DoubleArray(128)
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{id}")
    fun getEmbeddingById(@PathVariable id: UUID): ResponseEntity<EmbeddingResponse> {
        val embedding = embeddingService.getEmbeddingById(id)
        return embedding?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping()
    fun getEmbeddingProfileByDistance(@RequestBody request: SearchEmbeddingRequest): ResponseEntity<UUID?> {
        val profileId = embeddingService.getEmbeddingProfileByDistance(request)
        return ResponseEntity.ok(profileId)
    }

    @GetMapping
    fun getAllEmbeddings(): ResponseEntity<List<EmbeddingResponse>> {
        val embeddings = embeddingService.getAllEmbeddings()
        return ResponseEntity.ok(embeddings)
    }

    @GetMapping("/by-profile/{profileId}")
    fun getEmbeddingsByProfile(@PathVariable profileId: UUID): ResponseEntity<List<EmbeddingResponse>> {
        val embeddings = embeddingService.getEmbeddingsByProfileId(profileId)
        return ResponseEntity.ok(embeddings)
    }

    @DeleteMapping("/{id}")
    fun deleteEmbedding(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (embeddingService.deleteById(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}