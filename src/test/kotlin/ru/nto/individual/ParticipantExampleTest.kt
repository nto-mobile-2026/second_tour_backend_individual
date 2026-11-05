package ru.nto.individual

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

/** Open minimal examples. Enable each example after implementing that task. */
@SpringBootTest
class ParticipantExampleTest {
    @Autowired lateinit var read: PassReadService
    @Autowired lateinit var status: PassStatusService
    @Autowired lateinit var revoke: PassRevokeService
    @Autowired lateinit var clock: ScenarioClock
    @Autowired lateinit var jdbc: JdbcTemplate
    private val now = LocalDateTime.parse("2026-10-14T12:00:00")
    @Test fun `application connects to PostgreSQL`() {
        assertEquals("Europe/Moscow",clock.zone.id)
        assertEquals(1,jdbc.queryForObject("select 1",Int::class.java))
    }
    @BeforeEach fun prepare() {
        jdbc.execute("truncate scan, pass, visitor")
        jdbc.update("insert into visitor values (42,'Тестовый посетитель',null)")
        jdbc.update("insert into pass values (17,42,'TEST-QR',?,?,2,null)",now.minusHours(1),now.plusHours(1))
        clock.fix(now.atZone(clock.zone).toInstant())
    }
    @AfterEach fun restoreClock() { clock.fix(null) }
    @Disabled("Remove after implementing B1") @Test fun `B1 example`() {
        assertEquals("TEST-QR",read.getById(17).qrCode)
        assertNull(read.getById(17).visitor.organization)
        assertThrows(PassNotFoundException::class.java) { read.getById(999) }
    }
    @Disabled("Remove after implementing B2") @Test fun `B2 example`() {
        assertEquals(PassStatus.ACTIVE,status.status(17))
        jdbc.update("insert into scan values (1,17,?,'entry'),(2,17,?,'entry')",now,now)
        assertEquals(PassStatus.EXHAUSTED,status.status(17))
    }
    @Disabled("Remove after implementing B3") @Test fun `B3 example`() {
        assertEquals(PassRevokeDto(17,now),revoke.revoke(17))
        clock.fix(now.plusHours(1).atZone(clock.zone).toInstant())
        assertEquals(now,revoke.revoke(17).revokedAt)
        assertEquals(now,jdbc.queryForObject("select revoked_at from pass where id=17",LocalDateTime::class.java))
    }
}
