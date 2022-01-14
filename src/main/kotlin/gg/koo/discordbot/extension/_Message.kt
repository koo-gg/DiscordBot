package gg.koo.discordbot.extension

import discord4j.core.`object`.entity.Message

internal val inviteUrlReg =
    """(https?://)?(www.)?(discord.(gg|io|me|li)|discordapp.com/invite)/(.+[a-zA-Z0-6])""".toRegex()

fun Message.hasInvite(): Boolean {
    val matchResult = inviteUrlReg.find(content)

    return matchResult !== null
}

fun Message.getInviteCode(): String? {
    val matchResult = inviteUrlReg.find(content)

    return matchResult?.groupValues?.get(5)
}

fun Message.isCommand(): Boolean {
    return content.startsWith('.')
}