package org.poc.telegrambot.bot.users;

import org.poc.telegrambot.bot.users.entities.ActiveFilter;
import org.poc.telegrambot.bot.users.entities.States;
import org.poc.telegrambot.bot.users.entities.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SetUsersInterface implements UsersInterface{
    private final Set<User> users = new HashSet<>();
    @Override
    public boolean isUserExist(long chatId) {
        boolean result = false;
        for (User u : users) {
            if (u.chatId().equals(chatId)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void save(User user) {
        if(isUserExist(user.chatId())){
            users.remove(getUserByChatID(user.chatId()));
        }
        users.add(user);
    }
    @Override
    public User getUserByChatID(long chatId){
        for (User u : users){
            if (u.chatId().equals(chatId)){
                return u;
            }
        }
        User newUser = new User(chatId, States.EXIT, new ActiveFilter("", "", 0, null, null));
        users.add(newUser);
        return newUser;
    }
}
