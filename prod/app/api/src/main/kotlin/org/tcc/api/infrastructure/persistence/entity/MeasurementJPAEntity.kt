package org.tcc.api.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "profile_measurement")
class MeasurementEntity(
    @Id
    @Column(columnDefinition = "UUID")
    val id: UUID,

    @Column(nullable = false)
    var value: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    val profile: ProfileEntity
) {
    @NotNull
    @Column(name = "weight_value", nullable = false, precision = 5, scale = 2)
    open var weightValue: BigDecimal? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "measured_at")
    open var measuredAt: OffsetDateTime? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "recorded_at")
    open var recordedAt: OffsetDateTime? = null

    @Column(name = "notes", length = Integer.MAX_VALUE)
    open var notes: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: OffsetDateTime? = null
}
