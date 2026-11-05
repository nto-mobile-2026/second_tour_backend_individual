package ru.nto.individual

import org.springframework.stereotype.Service
import java.time.Clock

@Service
class PassStatusServiceImpl(private val passes: PassRepository, private val scans: ScanRepository, private val clock: Clock) : PassStatusService {
    override fun status(passId: Long): PassStatus = throw TaskNotImplementedException()
}
