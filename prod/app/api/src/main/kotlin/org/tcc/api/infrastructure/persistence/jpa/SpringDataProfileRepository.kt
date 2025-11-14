package org.tcc.api.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tcc.api.infrastructure.persistence.entity.ProfileEntity
import java.util.UUID

@Repository
interface SpringDataProfileRepository : JpaRepository<ProfileEntity, UUID> {
    fun findByEmail(email: String): ProfileEntity?
    fun existsByEmail(email: String): Boolean
}
