package org.tcc.api.infrastructure.persistence.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "profile_embedding")
class EmbeddingJPAEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", name = "id")
    var id: UUID? = null,

    @Column(name = "embedding", columnDefinition = "vector(128)", nullable = false)
    @JdbcTypeCode(SqlTypes.VECTOR)
    var embedding: DoubleArray,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "profile_id", nullable = false)
    var profile: ProfileJPAEntity
)