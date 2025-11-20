package org.tcc.api.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tcc.api.infrastructure.persistence.entity.ProfileJPAEntity
import java.util.UUID

@Repository
interface SpringDataProfileRepository : JpaRepository<ProfileJPAEntity, UUID> {
    fun findByName(name: String): ProfileJPAEntity?
}
