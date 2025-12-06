package org.tcc.api.application.embedding.dto

data class SearchEmbeddingRequest(
    val featureVector: DoubleArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchEmbeddingRequest

        if (!featureVector.contentEquals(other.featureVector)) return false

        return true
    }

    override fun hashCode(): Int {
        return featureVector.contentHashCode()
    }
}
