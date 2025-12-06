package org.tcc.api.domain.embedding

import java.util.UUID

data class Embedding (
    var id: UUID? = null,
    val profileId: UUID?,
    val featureVector: DoubleArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Embedding

        if (id != other.id) return false
        if (profileId != other.profileId) return false
        if (!featureVector.contentEquals(other.featureVector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + profileId.hashCode()
        result = 31 * result + featureVector.contentHashCode()
        return result
    }
}
