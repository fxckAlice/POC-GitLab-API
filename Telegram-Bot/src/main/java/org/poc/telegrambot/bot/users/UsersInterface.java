package org.poc.telegrambot.bot.users;

import org.poc.telegrambot.bot.users.entities.User;

public interface UsersInterface {
    boolean isUserExist(long chatId);
    User getUserByChatID(long chatId);
    void save(User user);
}
