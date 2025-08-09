package org.poc.telegrambot.bot;

import org.poc.telegrambot.bot.response.ResponseService;
import org.poc.telegrambot.bot.users.ListUsersInterface;
import org.poc.telegrambot.bot.users.UsersInterface;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final UsersInterface usersInterface;
    private final ResponseService responseService;

    public Bot(BotConfig botConfig, ListUsersInterface usersInterface, ResponseService responseService) {
        super(new DefaultBotOptions());
        this.botConfig = botConfig;
        this.usersInterface = usersInterface;
        this.responseService = responseService;
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        System.out.println(chatId);
        if (!usersInterface.isUserExist(chatId)) {
            usersInterface.addUser(chatId);
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            List<SendMessage> replies = responseService.getMembersAllResponse(chatId);
            replies.forEach(this::sendTextMessage);
        }
    }

    public void sendTextMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}