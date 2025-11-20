package org.tcc.api.domain.profile

import java.util.UUID

interface ProfileRepository {
    fun save(profile: Profile): Profile
    fun findById(id: UUID): Profile?
    fun findAll(): List<Profile>
    fun deleteById(id: UUID): Boolean
}
