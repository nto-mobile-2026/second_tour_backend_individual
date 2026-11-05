package ru.nto.individual

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicLong

@SpringBootApplication
class Application {
    @Bean fun clock() = ScenarioClock()
}

/** Ordinary Moscow clock; only the protected verifier fixes it temporarily. */
class ScenarioClock : Clock() {
    @Volatile private var fixed: Instant? = null
    private val reads = AtomicLong()
    private val moscow = ZoneId.of("Europe/Moscow")
    override fun getZone(): ZoneId = moscow
    override fun withZone(zone: ZoneId): Clock {
        if (zone == moscow) return this
        return object : Clock() {
            override fun getZone() = zone
            override fun withZone(zone: ZoneId) = this@ScenarioClock.withZone(zone)
            override fun instant() = this@ScenarioClock.instant()
        }
    }
    override fun instant(): Instant {
        reads.incrementAndGet()
        return fixed ?: Clock.system(moscow).instant()
    }
    fun readCount(): Long = reads.get()
    fun fix(instant: Instant?) { fixed = instant }
}

fun main(args: Array<String>) { runApplication<Application>(*args) }
