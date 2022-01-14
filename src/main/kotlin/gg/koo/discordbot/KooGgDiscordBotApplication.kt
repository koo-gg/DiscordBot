package gg.koo.discordbot

import kotlinx.coroutines.CoroutineExceptionHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KooGgDiscordBotApplication

fun main(args: Array<String>) {
    runApplication<KooGgDiscordBotApplication>(*args)
}