package org.tcc.api.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import org.tcc.api.domain.measurement.Measurement
import org.tcc.api.domain.measurement.MeasurementRepository
import org.tcc.api.infrastructure.persistence.entity.MeasurementEntity
import org.tcc.api.infrastructure.persistence.jpa.SpringDataMeasurementRepository
import org.tcc.api.infrastructure.persistence.jpa.SpringDataProfileRepository
import java.util.UUID

@Component
class JPAMeasurementRepository (
    private val springDataRepository: SpringDataMeasurementRepository,
    private val profileRepository: SpringDataProfileRepository
) : MeasurementRepository {

    override fun save(measurement: Measurement): Measurement {
        val id = measurement.id
        val profileEntity = profileRepository.findById(measurement.profileId)
            .orElseThrow { IllegalArgumentException("Profile not found") }

        val entity = springDataRepository.findById(id).orElse(null)?.apply {
            this.value = measurement.weightValue
        } ?: MeasurementEntity(
            id = id,
            value = measurement.weightValue,
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

    private fun MeasurementEntity.toDomain() = Measurement(
        id = this.id,
        weightValue = this.value,
        profileId = this.profile.id,
        recordedAt = this.recordedAt,
        notes = this.notes,
        createdAt = this.createdAt
    )
}