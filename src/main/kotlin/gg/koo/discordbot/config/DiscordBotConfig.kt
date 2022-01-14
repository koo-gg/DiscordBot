package gg.koo.discordbot.config

import discord4j.core.DiscordClient
import discord4j.core.event.domain.Event
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import gg.koo.discordbot.listener.Listener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kotlin.core.publisher.toMono

@Configuration
class DiscordBotConfig(
    val listeners: List<Listener<*>>
) {

    @Value("\${token}")
    lateinit var token: String

    @Bean
    fun initDiscordClient() {

        val client = DiscordClient.create(token)
            .gateway()
            .setEnabledIntents(
                IntentSet.of(
                    Intent.GUILDS,
                    Intent.GUILD_MESSAGES,
                    Intent.GUILD_MEMBERS,
                    Intent.GUILD_INVITES,
                    Intent.GUILD_VOICE_STATES,
                    Intent.GUILD_MESSAGE_REACTIONS))
            .login()
            .block()

        listeners.forEach {
            client!!.on(it.getEventType())
                .flatMap(it::test)
                .onErrorResume(it::handleError)
                .subscribe()
        }
    }
}