package ru.nto.individual

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController @RequestMapping("/api/passes")
class PassController(private val read: PassReadService, private val status: PassStatusService, private val revoke: PassRevokeService) {
    @GetMapping("/{id}") fun get(@PathVariable id: Long) = read.getById(id)
    @GetMapping("/{id}/status") fun status(@PathVariable id: Long) = mapOf("status" to status.status(id))
    @PostMapping("/{id}/revoke") fun revoke(@PathVariable id: Long) = revoke.revoke(id)
}

@RestControllerAdvice
class ApiErrors {
    @ExceptionHandler(PassNotFoundException::class) @ResponseStatus(HttpStatus.NOT_FOUND)
    fun missing() = mapOf("error" to "Pass not found")
    @ExceptionHandler(TaskNotImplementedException::class) @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    fun unfinished() = mapOf("error" to "Task not implemented")
}
