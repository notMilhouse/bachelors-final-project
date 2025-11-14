package org.tcc.api.domain.measurement

import java.util.UUID

interface MeasurementRepository {
    fun save(measurement: Measurement): Measurement
    fun findById(id: UUID): Measurement?
    fun findByProfileId(profileId: UUID): List<Measurement>
    fun findAll(): List<Measurement>
    fun deleteById(id: UUID): Boolean
}
