package org.tcc.api.domain.measurement

import java.sql.Timestamp
import java.util.UUID

data class Measurement (
    val id: UUID = UUID.randomUUID(),
    val profileId: UUID,
    val weightValue: Double,
    val recordedAt: Timestamp,
    val notes: String,
    val createdAt: Timestamp,
)
