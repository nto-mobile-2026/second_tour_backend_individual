package ru.nto.individual

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.aop.framework.Advised
import org.springframework.aop.support.AopUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.interceptor.TransactionInterceptor
import org.springframework.aop.PointcutAdvisor
import ru.nto.verifier.TaskVerifier
import java.time.LocalDateTime
import javax.sql.DataSource
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.transaction.CannotCreateTransactionException
import java.sql.SQLException

@Configuration
class VerificationConfiguration {
    @Bean fun verifier(dataSource: DataSource, read: PassReadService, status: PassStatusService, revoke: PassRevokeService,
                       clock: ScenarioClock, mapper: ObjectMapper): TaskVerifier {
        val calls = object : TaskVerifier.Calls {
            override fun call(task: Int, passId: Long): Any? = try {
                when(task) {
                    1 -> mapper.convertValue(read.getById(passId), Any::class.java)
                    2 -> status.status(passId).name
                    3 -> mapper.convertValue(revoke.revoke(passId), Any::class.java)
                    else -> error("Unknown task")
                }
            } catch (_: PassNotFoundException) { "NOT_FOUND" }
              catch (_: TaskNotImplementedException) { "NOT_IMPLEMENTED" }
              catch (failure: RuntimeException) {
                  if (isInfrastructureFailure(failure)) throw TaskVerifier.InfrastructureFailure(failure)
                  throw failure
              }
            override fun fixTime(time: LocalDateTime?) { clock.fix(time?.atZone(clock.zone)?.toInstant()) }
            override fun transactional(): Boolean = hasRevokeTransaction(revoke)
            override fun clockReads(): Long = clock.readCount()
        }
        val teaching = ClassPathResource("db/changelog/teaching.sql").inputStream.bufferedReader().use { it.readText() }
        return TaskVerifier(dataSource,calls,teaching)
    }
}

fun isInfrastructureFailure(failure: Throwable): Boolean {
    var cause: Throwable? = failure
    val visited = java.util.Collections.newSetFromMap(java.util.IdentityHashMap<Throwable,Boolean>())
    while (cause != null && visited.add(cause)) {
        if (cause is DataAccessResourceFailureException || cause is CannotCreateTransactionException ||
            (cause is SQLException && cause.sqlState?.startsWith("08") == true)) return true
        cause = cause.cause
    }
    return false
}

fun hasRevokeTransaction(service: PassRevokeService): Boolean {
    if (service !is Advised) return false
    val target = AopUtils.getTargetClass(service)
    val method = target.getMethod("revoke", java.lang.Long.TYPE)
    return service.advisors.any { advisor ->
        val interceptor = advisor.advice as? TransactionInterceptor ?: return@any false
        val applicable = advisor !is PointcutAdvisor ||
            (advisor.pointcut.classFilter.matches(target) && advisor.pointcut.methodMatcher.matches(method,target))
        val attribute = interceptor.transactionAttributeSource?.getTransactionAttribute(method,target)
        applicable && attribute != null && !attribute.isReadOnly &&
            attribute.propagationBehavior in listOf(0,3)
    }
}
