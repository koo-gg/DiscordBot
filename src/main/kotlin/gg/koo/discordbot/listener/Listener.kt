package gg.koo.discordbot.listener

import discord4j.core.`object`.entity.Entity
import discord4j.core.event.domain.Event
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

interface Listener<T: Event> {

    fun getEventType(): Class<T>

    fun listen(eventObject: Flow<T>): Flow<Any>

    fun test(event: Event): Mono<Any> =
        mono {
            val e = event as T
            listen(flowOf(e)).collect()
        }

    fun handleError(error: Throwable): Mono<Any> {
        val log = LoggerFactory.getLogger(Listener::class.java)

        log.error("Unable to process ${getEventType().simpleName}", error)
        return Mono.empty()
    }
}