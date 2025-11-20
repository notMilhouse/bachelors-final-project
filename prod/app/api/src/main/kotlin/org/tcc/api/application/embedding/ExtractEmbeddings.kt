package org.tcc.api.application.embedding

import org.springframework.stereotype.Component
import org.tcc.api.domain.embedding.Embedding
import java.awt.Image

@Component
class ExtractEmbeddings {
    fun retrieveEmbeddingVectorFromRequestImage(image: Image): DoubleArray {
        return DoubleArray(128);
    }
}
