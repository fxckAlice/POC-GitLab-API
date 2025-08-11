package org.poc.telegrambot.bot;

import org.poc.telegrambot.bot.response.ResponseService;
import org.poc.telegrambot.bot.users.SetUsersInterface;
import org.poc.telegrambot.bot.users.UsersInterface;
import org.poc.telegrambot.bot.users.entities.ActiveFilter;
import org.poc.telegrambot.bot.users.entities.States;
import org.poc.telegrambot.bot.users.entities.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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
            if(messageText.equals("/start")){
                sendTextMessage(responseService.startMessage(chatId));
                usersInterface.save(new User(chatId, States.START, new ActiveFilter("", null, 0, null, null)));
                return;
            }
            switch (user.state()) {
                case EXIT:
                    sendTextMessage(responseService.startMessage(chatId));
                    usersInterface.save(new User(chatId, States.START, user.activeFilter()));
                    break;
                case START:
                    switch (messageText) {
                        case "/members":
                            sendTextMessage(responseService.getMembersMenuResponse(chatId));
                            usersInterface.save(new User(chatId, States.MEMBERS_ALL, user.activeFilter()));
                            break;
                        case "/merge-requests":
                            sendTextMessage(responseService.getMRsMenuResponse(chatId));
                            usersInterface.save(new User(chatId, States.MERGE_REQUESTS_ALL, user.activeFilter()));
                            break;
                        case "/branches":
                            sendTextMessage(responseService.getBranchesMenuResponse(chatId));
                            usersInterface.save(new User(chatId, States.BRANCHES_ALL, user.activeFilter()));
                            break;
                        case "/commits":
                            sendTextMessage(responseService.getCommitsMenuResponse(chatId));
                            usersInterface.save(new User(chatId, States.COMMITS_ALL, user.activeFilter()));
                            break;
                        default:
                            sendWrongCommandMessage(user);
                    }
                    break;
                case MEMBERS_ALL:
                    switch (messageText){
                        case "/all":
                            sendTextMessage(responseService.getMembersAllResponse(chatId));
                            usersInterface.save(new User(chatId, States.EXIT, user.activeFilter()));
                            break;
                        case "/by-email":
                            sendTextMessage(responseService.getMemberByEmailInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.MEMBERS_BY_EMAIL, user.activeFilter()));
                            break;
                        default:
                            sendWrongCommandMessage(user);
                    }
                    break;
                case MEMBERS_BY_EMAIL:
                    sendTextMessage(responseService.getMemberByEmailResponse(chatId, messageText));
                    usersInterface.save(new User(chatId, States.EXIT, user.activeFilter()));
                    break;
                case MERGE_REQUESTS_ALL:
                    switch (messageText){
                        case "/all":
                            sendTextMessage(responseService.getMRsAllResponse(chatId));
                            usersInterface.save(new User(chatId, States.EXIT, user.activeFilter()));
                            break;
                        case "/by-iid":
                            sendTextMessage(responseService.getMRByIidInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.MERGE_REQUEST_BY_IID, user.activeFilter()));
                            break;
                        case "/by-filter":
                            sendTextMessage(responseService.getMRsByFilterMenuResponse(chatId, user.activeFilter()));
                            usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_FILTER, user.activeFilter()));
                            break;
                        default:
                            sendWrongCommandMessage(user);
                    }
                    break;
                case MERGE_REQUEST_BY_IID:
                    try{
                        sendTextMessage(responseService.getMRByIidResponse(chatId, Integer.parseInt(messageText)));
                    }
                    catch (NumberFormatException e){
                        sendTextMessage(responseService.getWrongIidFormatResponse(chatId));
                    }
                    usersInterface.save(new User(chatId, States.EXIT, user.activeFilter()));
                    break;
                case MERGE_REQUESTS_BY_FILTER:
                    switch (messageText){
                        case "/author-email":
                            sendTextMessage(responseService.getMRsByFilterEmailInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_EMAIL, user.activeFilter()));
                            break;
                        case "/since-date":
                            sendTextMessage(responseService.getMRsByFilterDateInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_SINCE, user.activeFilter()));
                            break;
                        case "/until-date":
                            sendTextMessage(responseService.getMRsByFilterDateInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_UNTIL, user.activeFilter()));
                            break;
                        case "/set-since-first-of-the-month":
                            ActiveFilter newMRFOTMFilter = new ActiveFilter(user.activeFilter().email(), user.activeFilter().branchName(), user.activeFilter().mrIid(), OffsetDateTime.now(ZoneOffset.UTC).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS), user.activeFilter().until());
                            sendTextMessage(responseService.getMRsByFilterMenuResponse(chatId, newMRFOTMFilter));
                            usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_FILTER, newMRFOTMFilter));
                            break;
                        case "/set-since-last-monday":
                            ActiveFilter newMREmailFilter = new ActiveFilter(user.activeFilter().email(), user.activeFilter().branchName(), user.activeFilter().mrIid(), OffsetDateTime.now(ZoneOffset.UTC)
                                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                    .truncatedTo(ChronoUnit.DAYS), user.activeFilter().until());
                            sendTextMessage(responseService.getMRsByFilterMenuResponse(chatId, newMREmailFilter));
                            usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_FILTER, newMREmailFilter));
                            break;
                        case "/send":
                            sendTextMessage(responseService.getMRsByFilterResponse(chatId, user.activeFilter()));
                            usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                            break;
                        case "/cancel":
                            sendTextMessage(responseService.cancelMessage(chatId));
                            usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                            break;
                        default:
                            sendWrongCommandMessage(user);
                    }
                    break;
                case MERGE_REQUESTS_BY_EMAIL:
                    ActiveFilter newMREmailFilter = new ActiveFilter(messageText, user.activeFilter().branchName(), user.activeFilter().mrIid(), user.activeFilter().since(), user.activeFilter().until());
                    sendTextMessage(responseService.getMRsByFilterMenuResponse(chatId, newMREmailFilter));
                    usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_FILTER, newMREmailFilter));
                    break;
                case MERGE_REQUESTS_BY_SINCE:
                    ActiveFilter newMRSinceFilter = user.activeFilter();
                    try{
                        newMRSinceFilter = new ActiveFilter(user.activeFilter().email(), user.activeFilter().branchName(), user.activeFilter().mrIid(), OffsetDateTime.parse(messageText).withOffsetSameInstant(ZoneOffset.UTC), user.activeFilter().until());
                    }
                    catch (DateTimeParseException e){
                        sendTextMessage(responseService.getWrongDateFormatResponse(chatId));
                    }
                    sendTextMessage(responseService.getMRsByFilterMenuResponse(chatId, newMRSinceFilter));
                    usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_FILTER, newMRSinceFilter));
                    break;
                case MERGE_REQUESTS_BY_UNTIL:
                    ActiveFilter newMRUntilFilter = user.activeFilter();
                    try{
                        newMRUntilFilter = new ActiveFilter(user.activeFilter().email(), user.activeFilter().branchName(), user.activeFilter().mrIid(), user.activeFilter().since(), OffsetDateTime.parse(messageText).withOffsetSameInstant(ZoneOffset.UTC));
                    }
                    catch (DateTimeParseException e){
                        sendTextMessage(responseService.getWrongDateFormatResponse(chatId));
                    }
                    sendTextMessage(responseService.getMRsByFilterMenuResponse(chatId, newMRUntilFilter));
                    usersInterface.save(new User(chatId, States.MERGE_REQUESTS_BY_FILTER, newMRUntilFilter));
                    break;
                case BRANCHES_ALL:
                    switch (messageText){
                        case "/all":
                            sendTextMessage(responseService.getBranchesAllResponse(chatId));
                            usersInterface.save(new User(chatId, States.EXIT, user.activeFilter()));
                            break;
                        case "/by-name":
                            sendTextMessage(responseService.getBranchByNameInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.BRANCH_BY_NAME, user.activeFilter()));
                            break;
                        default:
                            sendWrongCommandMessage(user);
                    }
                    break;
                case BRANCH_BY_NAME:
                    sendTextMessage(responseService.getBranchByNameResponse(chatId, messageText));
                    usersInterface.save(new User(chatId, States.EXIT, user.activeFilter()));
                    break;
                case COMMITS_ALL:
                    switch (messageText){
                        case "/by-branch-name":
                            sendTextMessage(responseService.getCommitsByBranchNameInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.COMMITS_BY_BRANCH_NAME, user.activeFilter()));
                            break;
                        case "/by-mr-iid":
                            sendTextMessage(responseService.getCommitsByMRIidInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.COMMITS_BY_MR_IID, user.activeFilter()));
                            break;
                        default :
                            sendWrongCommandMessage(user);
                    }
                    break;
                case COMMITS_BY_BRANCH_NAME:
                    ActiveFilter newCommitBranchFilter = new ActiveFilter(user.activeFilter().email(), messageText, user.activeFilter().mrIid(), user.activeFilter().since(), user.activeFilter().until());
                    sendTextMessage(responseService.getCommitsByBranchNameMenuResponse(chatId));
                    usersInterface.save(new User(chatId, States.COMMITS_BY_BRANCH_NAME_MENU, newCommitBranchFilter));
                    break;
                case COMMITS_BY_BRANCH_NAME_MENU:
                    switch (messageText){
                        case "/all":
                            sendTextMessage(responseService.getCommitsByBranchNameResponse(chatId, user.activeFilter().branchName()));
                            usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                            break;
                        case "/by-filter":
                            sendTextMessage(responseService.getCommitByBranchNameAuthorEmailInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.COMMITS_BY_BRANCH_NAME_AND_FILTER, user.activeFilter()));
                            break;
                        default:
                            sendWrongCommandMessage(user);
                            usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                            break;
                    }
                    break;
                case COMMITS_BY_BRANCH_NAME_AND_FILTER:
                    sendTextMessage(responseService.getCommitsByBranchNameAndFilterResponse(chatId, user.activeFilter().branchName(), messageText));
                    usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                    break;
                case COMMITS_BY_MR_IID:
                    try{
                        ActiveFilter newCommitMRFilter = new ActiveFilter(user.activeFilter().email(), user.activeFilter().branchName(), Integer.parseInt(messageText), user.activeFilter().since(), user.activeFilter().until());
                        sendTextMessage(responseService.getCommitsByMRIidMenuResponse(chatId));
                        usersInterface.save(new User(chatId, States.COMMITS_BY_MR_IID_MENU, newCommitMRFilter));
                    }
                    catch (NumberFormatException e){
                        sendTextMessage(responseService.getWrongIidFormatResponse(chatId));
                        usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                    }
                    break;
                case COMMITS_BY_MR_IID_MENU:
                    switch (messageText){
                        case "/all":
                            sendTextMessage(responseService.getCommitsByMRIidAllResponse(chatId, user.activeFilter().mrIid()));
                            usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                            break;
                        case "/by-filter":
                            sendTextMessage(responseService.getCommitsByMRIidAuthorEmailInputResponse(chatId));
                            usersInterface.save(new User(chatId, States.COMMITS_BY_MR_IID_AND_FILTER, user.activeFilter()));
                            break;
                        default:
                            sendWrongCommandMessage(user);
                            usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                    }
                    break;
                case COMMITS_BY_MR_IID_AND_FILTER:
                    sendTextMessage(responseService.getCommitsByMRIidAndFilterResponse(chatId, user.activeFilter().mrIid(), messageText));
                    usersInterface.save(new User(chatId, States.EXIT, new ActiveFilter("", null, 0, null, null)));
                    break;
            }
        }
    }
    public void sendWrongCommandMessage(User user) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(user.chatId()));
        message.setText("Wrong command. Please, try again.");
        sendTextMessage(message);
        usersInterface.save(new User(user.chatId(), States.EXIT, user.activeFilter()));
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