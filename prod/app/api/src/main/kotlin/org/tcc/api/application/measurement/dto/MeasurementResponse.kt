package org.tcc.api.application.measurement.dto

import java.util.UUID

data class MeasurementResponse(
    val id: UUID,
    val value: Double,
    val profileId: UUID
)
