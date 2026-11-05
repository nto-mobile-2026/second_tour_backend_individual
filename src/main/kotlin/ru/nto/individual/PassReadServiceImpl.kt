package ru.nto.individual

import org.springframework.stereotype.Service

@Service
class PassReadServiceImpl(private val passes: PassRepository) : PassReadService {
    override fun getById(passId: Long): PassDetailsDto = throw TaskNotImplementedException()
}
