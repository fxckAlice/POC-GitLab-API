package org.poc.telegrambot.bot.response;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseService {
    private final MessageConstructor messageConstructor;

    public ResponseService(MessageConstructor messageConstructor) {
        this.messageConstructor = messageConstructor;
    }

    public List<SendMessage> getMembersAllResponse(Long chatId) {
        List<SendMessage> result = new ArrayList<>();

        SendMessage title = new SendMessage();
        title.setChatId(String.valueOf(chatId));
        title.setText("All members");
        result.add(title);

        List<SendMessage> messages = messageConstructor.getMembersAll();
        for (SendMessage m : messages) {
            if (m.getChatId() == null || m.getChatId().isBlank()) {
                m.setChatId(String.valueOf(chatId));
            }
            result.add(m);
        }
        return result;
    }
}