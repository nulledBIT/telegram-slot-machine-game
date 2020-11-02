package com.soribot.slot.machine.telegram.bot.service

import com.soribot.slot.machine.telegram.bot.bot.BotSender
import com.soribot.slot.machine.telegram.bot.repository.Profile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Service
@ExperimentalCoroutinesApi
class ProfileManagementService(
    private val profileService: ProfileService,
    private val botSender: BotSender,
    private val leaderboardService: LeaderboardService
) {
    companion object {
        const val successfullySaved = "Bardzo dobrze przechowywane."
        const val editTrigger = "nastav"
        val recordTrigger = listOf("rekord", "kurtk")
        val lemonTrigger = listOf("citron", "cytryn")
        val barTrigger = listOf("bar")
        val cherryTrigger = listOf("ceresn", "wisnia")
    }

    fun start(update: Update) = updateProfile(update.message)

    private fun updateProfile(message: Message) {
        if (message.hasText() && message.isReply) {
            val commands = message.text.split("\\s".toRegex())

            if (commands.size == 3) {
                editProfileByCommand(message, commands[0], commands[1], commands[2])
            }
        }
    }

    private fun String.handleNumber(count: String) = if (contains(editTrigger)) {
        count.toInt()
    } else {
        null
    }

    private fun editProfileByCommand(message: Message, command: String, amount: String, field: String) =
        command.handleNumber(amount)?.also { convertedAmount ->
            when {
                recordTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { numberOfJackpots = convertedAmount }
                    .also { afterEdit(it, message) }
                lemonTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { threeLemons = convertedAmount }
                    .also { afterEdit(it, message) }
                barTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { threeBars = convertedAmount }
                    .also { afterEdit(it, message) }
                cherryTrigger.any { field.contains(it) } -> profileService.findByIdOrRegister(message.replyToMessage)
                    .apply { threeCherries = convertedAmount }
                    .also { afterEdit(it, message) }
            }
        }

    fun afterEdit(profile: Profile, message: Message) {
        profileService.save(profile)
        botSender.textAsync(message.chatId, successfullySaved)
        leaderboardService.sendLeaderboards(message)
    }
}