package org.tcc.api.application.embedding.dto

import java.awt.Image
import java.sql.Timestamp
import java.util.UUID

data class CreateEmbeddingRequest(
    val image: Image,
    val profileId: UUID
)
