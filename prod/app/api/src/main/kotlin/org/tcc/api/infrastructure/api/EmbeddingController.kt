package org.tcc.api.infrastructure.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tcc.api.application.embedding.EmbeddingService
import org.tcc.api.application.embedding.dto.CreateEmbeddingRequest
import org.tcc.api.application.embedding.dto.EmbeddingResponse
import java.util.UUID

@RestController
@RequestMapping("/api/embeddings")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class EmbeddingController(
    private val embeddingService: EmbeddingService
) {

    @PostMapping
    fun createEmbedding(@RequestBody request: CreateEmbeddingRequest): ResponseEntity<EmbeddingResponse> {
        return try {
            val embedding = embeddingService.createEmbedding(request)
            ResponseEntity.status(HttpStatus.CREATED).body(embedding)
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