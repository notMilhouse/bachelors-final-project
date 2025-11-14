package org.tcc.api.domain.profile

import java.util.UUID

interface ProfileRepository {
    fun save(profile: Profile): Profile
    fun findById(id: UUID): Profile?
    fun findByEmail(email: String): Profile?
    fun findAll(): List<Profile>
    fun existsByEmail(email: String): Boolean
    fun update(profile: Profile): Profile
    fun deleteById(id: UUID): Boolean
}
