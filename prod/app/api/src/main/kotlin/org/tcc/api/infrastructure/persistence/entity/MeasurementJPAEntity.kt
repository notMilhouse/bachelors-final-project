package org.tcc.api.infrastructure.persistence.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.*

@Entity
@Table(name = "profile_measurement")
class MeasurementJPAEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", nullable = false)
    val id: UUID? = null,

    @Column(name="weight_value", nullable = false)
    var value: BigDecimal,

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "measured_at")
    var measuredAt: Timestamp,

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "recorded_at")
    var recordedAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "profile_id", nullable = false)
    var profile: ProfileJPAEntity,
)
