package org.tcc.api.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import org.tcc.api.domain.measurement.Measurement
import org.tcc.api.domain.measurement.MeasurementRepository
import org.tcc.api.infrastructure.persistence.entity.MeasurementJPAEntity
import org.tcc.api.infrastructure.persistence.jpa.SpringDataMeasurementRepository
import org.tcc.api.infrastructure.persistence.jpa.SpringDataProfileRepository
import java.sql.Timestamp
import java.util.UUID

@Component
class JPAMeasurementRepository (
    private val springDataRepository: SpringDataMeasurementRepository,
    private val profileRepository: SpringDataProfileRepository
) : MeasurementRepository {

    override fun save(measurement: Measurement): Measurement {
        val profileEntity = profileRepository.findById(measurement.profileId)
            .orElseThrow { IllegalArgumentException("Profile not found with id: ${measurement.profileId}") }

        // Always create new measurement
        val entity = MeasurementJPAEntity(
            id = null,
            value = measurement.weightValue,
            measuredAt = measurement.measuredAt,
            recordedAt = Timestamp(System.currentTimeMillis()),
            profile = profileEntity
        )

        val savedEntity = springDataRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(id: UUID): Measurement? {
        return springDataRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByProfileId(profileId: UUID): List<Measurement> {
        return springDataRepository.findByProfileId(profileId)
            .map { it.toDomain() }
    }

    override fun findAll(): List<Measurement> {
        return springDataRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: UUID): Boolean {
        return if (springDataRepository.existsById(id)) {
            springDataRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    private fun MeasurementJPAEntity.toDomain() = Measurement(
        id = this.id,
        weightValue = this.value,
        profileId = this.profile.id!!,
        measuredAt = this.measuredAt,
        recordedAt = this.recordedAt
    )
}