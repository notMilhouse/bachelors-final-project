package org.tcc.api.domain.measurement

import java.util.UUID

interface MeasurementRepository {
    fun findAll(): List<Measurement>
    fun findById(id: UUID): Measurement?
    fun register(measurement: Measurement)
    fun update(measurement: Measurement): Measurement?
    fun deleteById(id: UUID): Measurement?
}