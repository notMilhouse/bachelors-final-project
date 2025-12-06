package org.tcc.api.application.embedding

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class ExtractEmbeddings {
    fun retrieveEmbeddingVectorFromRequestImage(image: MultipartFile): DoubleArray {
        return DoubleArray(128);
    }
}
