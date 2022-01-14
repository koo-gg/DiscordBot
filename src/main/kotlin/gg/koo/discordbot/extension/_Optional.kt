package gg.koo.discordbot.extension

import java.util.*

fun <T> Optional<T>.orElsNull(): T? = this.orElse(null)