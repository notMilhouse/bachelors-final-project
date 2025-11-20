package org.tcc.api.infrastructure.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tcc.api.application.measurement.MeasurementService
import org.tcc.api.application.measurement.dto.CreateMeasurementRequest
import org.tcc.api.application.measurement.dto.MeasurementResponse
import java.util.UUID

@RestController
@RequestMapping("/api/measurements")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class MeasurementController(
    private val measurementService: MeasurementService
) {

    @PostMapping
    fun createMeasurement(@RequestBody request: CreateMeasurementRequest): ResponseEntity<MeasurementResponse> {
        return try {
            val measurement = measurementService.createMeasurement(request)
            ResponseEntity.status(HttpStatus.CREATED).body(measurement)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{id}")
    fun getMeasurementById(@PathVariable id: UUID): ResponseEntity<MeasurementResponse> {
        val measurement = measurementService.getMeasurementById(id)
        return measurement?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAllMeasurements(): ResponseEntity<List<MeasurementResponse>> {
        val measurements = measurementService.getAllMeasurements()
        return ResponseEntity.ok(measurements)
    }

    @GetMapping("/by-profile/{profileId}")
    fun getMeasurementsByProfile(@PathVariable profileId: UUID): ResponseEntity<List<MeasurementResponse>> {
        val measurements = measurementService.getMeasurementsByProfileId(profileId)
        return ResponseEntity.ok(measurements)
    }

    @DeleteMapping("/{id}")
    fun deleteMeasurement(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (measurementService.deleteById(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}