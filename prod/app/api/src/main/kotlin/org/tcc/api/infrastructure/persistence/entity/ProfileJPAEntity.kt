package org.tcc.api.infrastructure.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.sql.Timestamp
import java.util.UUID

@Entity
@Table(name = "profile")
class ProfileEntity(
    @Id
    @Column(columnDefinition = "UUID")
    val id: UUID,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @NotNull
    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @OneToMany(mappedBy = "profile", cascade = [CascadeType.ALL], orphanRemoval = true)
    val measurements: MutableList<MeasurementEntity> = mutableListOf()
) {
    @Size(max = 500)
    @Column(name = "profile_picture_path", length = 500)
    open var profilePicturePath: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    open var createdAt: Timestamp = Timestamp(System.currentTimeMillis())

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    open var updatedAt: Timestamp? = null
}
