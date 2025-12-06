package org.tcc.api.application.embedding.dto

import java.util.UUID

data class EmbeddingResponse(
    val id: UUID? = null,
    val featureVector: DoubleArray,
    val profileId: UUID?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmbeddingResponse

        if (id != other.id) return false
        if (!featureVector.contentEquals(other.featureVector)) return false
        if (profileId != other.profileId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + featureVector.contentHashCode()
        result = 31 * result + profileId.hashCode()
        return result
    }
}
