package ru.nto.individual

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.nto.encoder.Encoder
import ru.nto.verifier.TaskVerifier

@RestController
class CodeController(private val verifier: TaskVerifier) {
    private val encoder = Encoder()
    @GetMapping("/code/task1/{participantId}", produces = ["text/plain"])
    fun task1(@PathVariable participantId: Long) = code(1,participantId)
    @GetMapping("/code/task2/{participantId}", produces = ["text/plain"])
    fun task2(@PathVariable participantId: Long) = code(2,participantId)
    @GetMapping("/code/task3/{participantId}", produces = ["text/plain"])
    fun task3(@PathVariable participantId: Long) = code(3,participantId)
    fun code(task: Int, participantId: Long): String = verifier.verify(task) { report ->
        encoder.encode(Encoder.SUITE_VERSION,task,participantId,report)
    }
}

@RestControllerAdvice
class VerificationErrors {
    @ExceptionHandler(TaskVerifier.Busy::class) @ResponseStatus(HttpStatus.CONFLICT)
    fun busy() = mapOf("error" to "Verification already running")
    @ExceptionHandler(TaskVerifier.Unavailable::class) @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun unavailable() = mapOf("error" to "Verification unavailable")
}
