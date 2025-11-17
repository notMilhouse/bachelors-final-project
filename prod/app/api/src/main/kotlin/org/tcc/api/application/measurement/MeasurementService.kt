package org.tcc.api.application.measurement

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tcc.api.application.measurement.dto.CreateMeasurementRequest
import org.tcc.api.application.measurement.dto.MeasurementResponse
import org.tcc.api.domain.measurement.Measurement
import org.tcc.api.domain.measurement.MeasurementRepository
import org.tcc.api.domain.profile.ProfileRepository
import java.sql.Timestamp
import java.util.UUID

@Service
@Transactional
class MeasurementService(
    private val measurementRepository: MeasurementRepository,
    private val profileRepository: ProfileRepository
) {

    fun createMeasurement(request: CreateMeasurementRequest): MeasurementResponse {
        val profileId = request.profileId

        profileRepository.findById(profileId)
            ?: throw IllegalArgumentException("Profile not found")

        val measurement = Measurement(
            weightValue = request.value,
            profileId = profileId,
            id = UUID.randomUUID(),
            recordedAt = request.timeOfRecord,
            notes = "",
            createdAt = Timestamp(System.currentTimeMillis())
        )

        val savedMeasurement = measurementRepository.save(measurement)
        return savedMeasurement.toResponse()
    }

    @Transactional(readOnly = true)
    fun getMeasurementById(id: UUID): MeasurementResponse? {
        return measurementRepository.findById(id)?.toResponse()
    }

    @Transactional(readOnly = true)
    fun getMeasurementsByProfileId(profileId: UUID): List<MeasurementResponse> {
        return measurementRepository.findByProfileId(profileId)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getAllMeasurements(): List<MeasurementResponse> {
        return measurementRepository.findAll().map { it.toResponse() }
    }


    fun deleteById(id: UUID): Boolean {
        return measurementRepository.deleteById(id)
    }

    private fun Measurement.toResponse() = MeasurementResponse(
        id = this.id,
        value = this.weightValue,
        profileId = this.profileId
    )
}