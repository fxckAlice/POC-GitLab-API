package org.poc.telegrambot.bot.response;

import org.poc.telegrambot.bot.users.entities.ActiveFilter;
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
    public SendMessage cancelMessage(Long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Operation canceled.");
        message.setReplyMarkup(new ReplyKeyboardRemove(true));
        return message;
    }
    public SendMessage getMembersMenuResponse(Long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Choose option:");
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/all");
        row1.add("/by-email");
        rows.add(row1);

        message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .build()
        );
        return message;
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
        if(messages.isEmpty()){
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Nothing to show");
            message.setReplyMarkup(new ReplyKeyboardRemove(true));
            result.add(message);
        }
        return result;
    }
    public SendMessage getMemberByEmailInputResponse(Long chatId){
        SendMessage message = new SendMessage();
        message.setReplyMarkup(new ReplyKeyboardRemove(true));
        message.setChatId(String.valueOf(chatId));
        message.setText("Send me the email of user whose info you want to get");
        return message;
    }
    public SendMessage getMemberByEmailResponse(Long chatId, String email){
        SendMessage message = messageConstructor.getMemberByEmail(email);
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(new ReplyKeyboardRemove(true));
        return message;
    }
    public SendMessage getMRsMenuResponse(Long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Choose option:");
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/all");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/by-iid");
        row2.add("/by-filter");

        rows.add(row1);
        rows.add(row2);

        message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .build()
        );
        return message;
    }
    @SuppressWarnings("all")
    public List<SendMessage> getMRsAllResponse(Long chatId) {
        List<SendMessage> result = new ArrayList<>();

        SendMessage title = new SendMessage();
        title.setChatId(String.valueOf(chatId));
        title.setText("All Merge Requests");
        result.add(title);

        List<SendMessage> messages = messageConstructor.getMRsAll();
        for (SendMessage m : messages) {
            if (m.getChatId() == null || m.getChatId().isBlank()) {
                m.setChatId(String.valueOf(chatId));
            }
            m.setReplyMarkup(new ReplyKeyboardRemove(true));
            result.add(m);
        }
        if(messages.isEmpty()){
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Nothing to show");
            message.setReplyMarkup(new ReplyKeyboardRemove(true));
            result.add(message);
        }
        return result;
    }
    public SendMessage getMRByIidInputResponse(Long chatId){
        SendMessage message = new SendMessage();
        message.setReplyMarkup(new ReplyKeyboardRemove(true));
        message.setChatId(String.valueOf(chatId));
        message.setText("Send me the iid of merge request whose info you want to get");
        return message;
    }
    public SendMessage getMRByIidResponse(Long chatId, int iid){
        SendMessage message = messageConstructor.getMRByIid(iid);
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(new ReplyKeyboardRemove(true));
        return message;
    }
    public List<SendMessage> getMRsByFilterMenuResponse(Long chatId, ActiveFilter activeFilter){
        List<SendMessage> result = new ArrayList<>();

        SendMessage filterMessage = new SendMessage();
        filterMessage.setChatId(String.valueOf(chatId));
        filterMessage.setText("Your filters:" +
                "\n\nAuthor Email:\t" + (activeFilter.email().isEmpty() ? "none" : activeFilter.email()) +
                "\nSince Date:\t" + (activeFilter.since() == null ? "none" : messageConstructor.constructTime(activeFilter.since())) +
                "\nUntil Date:\t" + (activeFilter.until() == null ? "none" : messageConstructor.constructTime(activeFilter.until())) +
                "\n\nAt least one filter must be specified."
        );
        result.add(filterMessage);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Choose filter to edit:");

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("/author-email");
        row1.add("/since-date");
        row1.add("/until-date");
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/set-since-first-of-the-month");
        row2.add("/set-since-last-monday");
        rows.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("/cancel");
        row3.add("/send");
        rows.add(row3);

        message.setReplyMarkup(ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .build()
        );
        result.add(message);
        return result;
    }
    @SuppressWarnings("all")
    public List<SendMessage> getMRsByFilterResponse(Long chatId, ActiveFilter activeFilter){
        List<SendMessage> result = new ArrayList<>();

        SendMessage title = new SendMessage();
        title.setChatId(String.valueOf(chatId));
        title.setText("Merge Requests by Filter");
        result.add(title);

        List<SendMessage> messages = messageConstructor.getMRsByFilter(activeFilter.email(), activeFilter.since(), activeFilter.until());
        for(SendMessage m : messages){
            if (m.getChatId() == null || m.getChatId().isEmpty()){
                m.setChatId(String.valueOf(chatId));
            }
            m.setReplyMarkup(new ReplyKeyboardRemove(true));
            result.add(m);
        }
        if(messages.isEmpty()){
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Nothing to show");
            message.setReplyMarkup(new ReplyKeyboardRemove(true));
            result.add(message);
        }
        return result;
    }
    public SendMessage getMRsByFilterEmailInputResponse(Long chatId){
        SendMessage message = new SendMessage();
        message.setReplyMarkup(new ReplyKeyboardRemove(true));
        message.setChatId(String.valueOf(chatId));
        message.setText("Send me the email of author whose merge requests you want to get");
        return message;
    }
    public SendMessage getMRsByFilterDateInputResponse(Long chatId){
        SendMessage message = new SendMessage();
        message.setReplyMarkup(new ReplyKeyboardRemove(true));
        message.setChatId(String.valueOf(chatId));
        message.setText("Send me the date in format \t<b>yyyy-MM-dd'T'HH:mmXXX</b>" +
                "\nExample: \t<b>2025-08-10T21:37+03:00</b>");
        message.setParseMode("HTML");
        return message;
    }
    public SendMessage getWrongDateFormatResponse(Long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Wrong date format!");
        return message;
    }
}