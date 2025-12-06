package org.tcc.api.application.embedding.dto

import org.springframework.web.multipart.MultipartFile
import java.util.*

data class CreateEmbeddingRequest(
    val image: MultipartFile,
    val profileId: UUID
)
