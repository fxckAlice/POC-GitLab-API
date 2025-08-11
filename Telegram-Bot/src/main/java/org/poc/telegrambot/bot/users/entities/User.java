package org.poc.telegrambot.bot.users.entities;

public record User(
        Long chatId,
        States state,
        ActiveFilter activeFilter
) {
}
