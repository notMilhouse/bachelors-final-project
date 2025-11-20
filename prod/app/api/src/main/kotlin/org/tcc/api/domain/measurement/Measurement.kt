package org.tcc.api.domain.measurement

import java.math.BigDecimal
import java.sql.Timestamp
import java.util.UUID

data class Measurement (
    var id: UUID? = null,
    val profileId: UUID,
    val weightValue: BigDecimal,
    val measuredAt: Timestamp,
    val recordedAt: Timestamp = Timestamp(System.currentTimeMillis()),
)
