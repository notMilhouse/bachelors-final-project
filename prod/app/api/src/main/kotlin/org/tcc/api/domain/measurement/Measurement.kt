package org.tcc.api.domain.measurement

import java.util.UUID

data class Measurement (
    val id: UUID,
    val value: Double,
    val unit: String,
    val profileId: UUID,
)