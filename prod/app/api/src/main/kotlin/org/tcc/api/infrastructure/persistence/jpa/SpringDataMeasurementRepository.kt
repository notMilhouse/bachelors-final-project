package org.tcc.api.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tcc.api.infrastructure.persistence.entity.MeasurementEntity
import java.util.UUID


@Repository
interface SpringDataMeasurementRepository : JpaRepository<MeasurementEntity, UUID> {
    fun findByProfileId(profileId: UUID): List<MeasurementEntity>
}
