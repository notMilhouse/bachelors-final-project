package org.tcc.api.application.measurement.dto

import java.math.BigDecimal
import java.sql.Timestamp
import java.util.UUID

data class CreateMeasurementRequest(
    val value: BigDecimal,
    val profileId: UUID,
    val measuredAt: Timestamp,
)
