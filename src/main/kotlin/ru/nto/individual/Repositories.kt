package ru.nto.individual

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface PassRepository : JpaRepository<Pass, Long>
interface VisitorRepository : JpaRepository<Visitor, Long>
interface ScanRepository : JpaRepository<Scan, Long> {
    fun countByPassIdAndDirectionAndScannedAtLessThanEqual(passId: Long, direction: String, now: LocalDateTime): Long
}
