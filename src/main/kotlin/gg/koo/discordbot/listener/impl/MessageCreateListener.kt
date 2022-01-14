package gg.koo.discordbot.listener.impl

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.Invite
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.BanQuerySpec
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import gg.koo.discordbot.exception.UnknownInviteException
import gg.koo.discordbot.extension.getInviteCode
import gg.koo.discordbot.extension.hasInvite
import gg.koo.discordbot.extension.isCommand
import gg.koo.discordbot.extension.orElsNull
import gg.koo.discordbot.listener.Listener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component

@Component
class MessageCreateListener: Listener<MessageCreateEvent> {

    private data class Context(
        val client: GatewayDiscordClient,
        val guild: Guild,
        val channel: MessageChannel,
        val member: Member,
        val message: Message
    )

    override fun getEventType(): Class<MessageCreateEvent> {
        return MessageCreateEvent::class.java
    }

    override fun listen(eventObject: Flow<MessageCreateEvent>): Flow<Any> = eventObject
        .mapNotNull { event ->
            Context(
                client = event.client,
                guild = event.guild.awaitSingleOrNull() ?: return@mapNotNull null,
                channel = event.message.channel.awaitSingleOrNull() ?: return@mapNotNull null,
                member = event.member.orElsNull() ?: return@mapNotNull null,
                message = event.message
            )
        }
        .filter { context -> !context.member.isBot }
        .buffer()
        .map {
            println(it.message)
            when {
                it.message.hasInvite() -> executeInviteWork(it)
                it.message.isCommand() -> it.message
                else -> it.message
            }
        }
        .catch {
            println(it.message)
        }

    private suspend fun executeInviteWork(ctx: Context) {
        val inviteCode = ctx.message.getInviteCode()

        val invite = ctx.client
            .getInvite(inviteCode!!)
            .doOnError { ctx.message.delete().subscribe() }
            .awaitSingle()

        if (invite.guildId.get() != Snowflake.of(925033252580896768)) {
            ctx.member.ban(
                BanQuerySpec.builder()
                    .reason("타 디스코드 링크")
                    .deleteMessageDays(7)
                    .build()
            ).subscribe()

            ctx.channel.createMessage(
                EmbedCreateSpec.builder()
                    .author(ctx.member.username, ctx.member.defaultAvatarUrl , ctx.member.avatarUrl)
                    .title("dd")
                    .color(Color.BLUE)
                    .build()
            ).subscribe()
        }
    }
}
