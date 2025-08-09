package org.poc.telegrambot.bot.users;

public interface UsersInterface {
    boolean isUserExist(long chatId);
    void addUser(long chatId);
}
