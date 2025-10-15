package org.tcc.api.domain.profile

import java.util.UUID

interface ProfileRepository {
    fun findAll(): List<Profile>
    fun findById(id: UUID): Profile?
    fun register(measurement: Profile)
    fun update(measurement: Profile): Profile?
    fun deleteById(id: UUID): Profile?
}