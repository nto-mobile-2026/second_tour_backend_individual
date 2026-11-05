package ru.nto.individual

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity @Table(name = "visitor")
class Visitor(
    @Id var id: Long = 0,
    var fullName: String = "",
    var organization: String? = null,
)

@Entity @Table(name = "pass")
class Pass(
    @Id var id: Long = 0,
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "visitor_id") var visitor: Visitor = Visitor(),
    var qrCode: String = "",
    var validFrom: LocalDateTime = LocalDateTime.MIN,
    var validTo: LocalDateTime = LocalDateTime.MIN,
    var maxEntries: Int = 1,
    var revokedAt: LocalDateTime? = null,
)

@Entity @Table(name = "scan")
class Scan(
    @Id var id: Long = 0,
    var passId: Long = 0,
    var scannedAt: LocalDateTime = LocalDateTime.MIN,
    var direction: String = "entry",
)

data class VisitorDto(val id: Long, val fullName: String, val organization: String?)
data class PassDetailsDto(
    val id: Long, val qrCode: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") val validFrom: LocalDateTime,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") val validTo: LocalDateTime,
    val maxEntries: Int,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") val revokedAt: LocalDateTime?,
    val visitor: VisitorDto,
)
data class PassRevokeDto(val id: Long, @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") val revokedAt: LocalDateTime)
enum class PassStatus { ACTIVE, NOT_STARTED, EXPIRED, EXHAUSTED, BLOCKED }
class PassNotFoundException : RuntimeException()
class TaskNotImplementedException : RuntimeException()
interface PassReadService { fun getById(passId: Long): PassDetailsDto }
interface PassStatusService { fun status(passId: Long): PassStatus }
interface PassRevokeService { fun revoke(passId: Long): PassRevokeDto }
