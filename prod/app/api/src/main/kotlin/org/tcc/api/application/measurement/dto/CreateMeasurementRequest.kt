package org.tcc.api.application.measurement.dto

import java.sql.Timestamp
import java.util.UUID

data class CreateMeasurementRequest(
    val value: Double,
    val profileId: UUID,
    val timeOfRecord: Timestamp,
)
