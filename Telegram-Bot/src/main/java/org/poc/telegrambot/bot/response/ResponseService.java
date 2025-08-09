package org.poc.telegrambot.bot.response;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseService {
    private final MessageConstructor messageConstructor;

    public ResponseService(MessageConstructor messageConstructor) {
        this.messageConstructor = messageConstructor;
    }

    @SuppressWarnings("all")
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
            m.setReplyMarkup(new ReplyKeyboardRemove(true));
            result.add(m);
        }
        return result;
    }
    public SendMessage startMessage(Long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Hello! I'm a bot for GitLab API. " +
                "You can get information about members, merge requests, branches and commits.");
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/members");
        row1.add("/branches");
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/merge-requests");
        row2.add("/commits");
        rows.add(row2);

        message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .build()
        );
        return message;
    }

}