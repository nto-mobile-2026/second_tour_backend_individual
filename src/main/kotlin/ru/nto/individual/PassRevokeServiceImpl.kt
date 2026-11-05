package ru.nto.individual

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
class PassRevokeServiceImpl(private val passes: PassRepository, private val clock: Clock) : PassRevokeService {
    @Transactional
    override fun revoke(passId: Long): PassRevokeDto = throw TaskNotImplementedException()
}
