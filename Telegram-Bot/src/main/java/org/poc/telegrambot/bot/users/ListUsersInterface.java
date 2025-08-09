package org.poc.telegrambot.bot.users;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ListUsersInterface implements UsersInterface{
    private final List<Long> users = new ArrayList<>();
    @Override
    public boolean isUserExist(long chatId) {
        return users.contains(chatId);
    }

    @Override
    public void addUser(long chatId) {
        users.add(chatId);
    }

}
