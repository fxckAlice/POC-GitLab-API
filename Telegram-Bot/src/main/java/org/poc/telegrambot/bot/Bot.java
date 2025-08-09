package org.poc.telegrambot.bot;

import org.poc.telegrambot.bot.response.ResponseService;
import org.poc.telegrambot.bot.users.SetUsersInterface;
import org.poc.telegrambot.bot.users.UsersInterface;
import org.poc.telegrambot.bot.users.entities.States;
import org.poc.telegrambot.bot.users.entities.User;
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

    @SuppressWarnings("all")
    public Bot(BotConfig botConfig, SetUsersInterface usersInterface, ResponseService responseService) {
        super(new DefaultBotOptions());
        this.botConfig = botConfig;
        this.usersInterface = usersInterface;
        this.responseService = responseService;
    }

    @SuppressWarnings("all")
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            usersInterface.getUserByChatID(chatId);
            User user = usersInterface.getUserByChatID(chatId);
            String messageText = update.getMessage().getText();
            switch (user.state()) {
                case EXIT:
                    sendTextMessage(responseService.startMessage(chatId));
                    usersInterface.save(new User(chatId, States.START, user.activeFilter()));
                case START:
                    switch (messageText) {
                        case "/members":
                            sendTextMessage(responseService.getMembersAllResponse(chatId));
                            usersInterface.save(new User(chatId, States.EXIT, user.activeFilter()));
                        case "/merge-requests":
                        case "/branches":
                        case "/commits":
                        default:
                    }
                case MEMBERS_ALL:
                case MEMBERS_BY_EMAIL:
                case MERGE_REQUESTS_ALL:
                case MERGE_REQUESTS_BY_FILTER:
                case MERGE_REQUEST_BY_IID:
                case BRANCHES_ALL:
                case BRANCH_BY_NAME:
                case COMMITS_BY_BRANCH_NAME_AND_FILTER:
                case COMMITS_BY_BRANCH_NAME:
                case COMMITS_BY_MR_IID_AND_FILTER:
                case COMMITS_BY_MR_IID:
                case EMAIL_FILTER:
                case SINCE_FILTER:
                case UNTIL_FILTER:
                case BRANCH_FILTER:
                case MERGE_REQUEST_FILTER:
            }
        }
    }

    public void sendTextMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void sendTextMessage(List<SendMessage> messages) {
        for (SendMessage m : messages) {
            sendTextMessage(m);
        }
    }
}