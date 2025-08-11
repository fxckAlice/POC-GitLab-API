package org.poc.telegrambot.bot.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poc.telegrambot.client.GitLabApiClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageConstructor {

    private final GitLabApiClient gitLabApiClient;

    public MessageConstructor(GitLabApiClient gitLabApiClient) {
        this.gitLabApiClient = gitLabApiClient;
    }

    private JsonNode parseJson(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public String constructTime(OffsetDateTime dateTime){
        return dateTime.getYear() + "-" +
                dateTime.getMonthValue() + "-" +
                dateTime.getDayOfMonth() + " " +
                dateTime.getHour() + ":" +
                dateTime.getMinute() + ":" +
                dateTime.getSecond();
    }
    public String constructTime(Duration duration){
        return duration.toHours() + "h " +
                duration.toMinutes() % 60 + "m " +
                duration.getSeconds() % 60 + "s";
    }
    private String constructMember(JsonNode member, int i){
        String accessLevel = switch (member.get("accessLevel").asInt()){
            case 50 -> "Owner";
            case 40 -> "Maintainer";
            case 30 -> "Developer";
            case 20 -> "Reporter";
            case 15 -> "Planer";
            case 10 -> "Guest";
            case 5 -> "Minimal Access";
            case 0 -> "No Access";
            default -> "Unknown";
        };
        return "#" + (i + 1) +
                "\n\nID:    \t<a href=\"" + member.get("webUrl").asText() + "\">" + member.get("id").asLong() + "</a> " +
                "\nUsername:    \t" + member.get("username").asText() +
                "\nEmail:    \t" + member.get("email").asText() +
                "\nAccess Level:    \t" + accessLevel +
                "\nMerge Requests Created:    \t" + member.get("createdMRs").asInt();
    }
    private String constructMR(JsonNode mr, int i){
        return "#" + (i + 1) +
                "\n\nID:    \t<a href=\"" + mr.get("webUrl").asText() + "\">" + mr.get("id").asInt() + "</a> " +
                "\nIID:    \t" + mr.get("iid").asInt() +
                "\nTitle:    \t" + mr.get("title").asText() +
                "\nAuthor:    \t<a href=\"" + mr.get("authorUrl").asText() + "\">" + mr.get("authorEmail").asText() + "</a> " +
                "\nCreated:    \t" + constructTime(OffsetDateTime.parse(mr.get("createdAt").asText())) +
                "\nMerged:    \t" + constructTime(OffsetDateTime.parse(mr.get("mergedAt").asText())) +
                "\nTarget Branch:    \t<a href=\"" + mr.get("targetBranchUrl").asText() + "\">" + mr.get("targetBranchName").asText() + "</a> " +
                "\nState:    \t" + mr.get("state").asText() +
                "\nCommits:    \t" + mr.get("commitsCount").asInt() +
                "\nTime Taken:    \t" + constructTime(Duration.parse(mr.get("timeTaken").asText()));
    }
    private String constructBranch(JsonNode branch, int i){
        return "#" + (i + 1) +
                "\n\nName:    \t<a href=\"" + branch.get("webUrl").asText() + "\">" + branch.get("name").asText() + "</a>" +
                "\nLast Commit:    \t<a href=\"" + branch.get("lastCommitUrl").asText() + "\">" + branch.get("lastCommitSha").asText() + "</a>";
    }
    public List<SendMessage> getMembersAll(){
        List<SendMessage> messages = new ArrayList<>();
        try{
            JsonNode allMembers = parseJson(gitLabApiClient.getMembersAll());
            for (int i = 0; i < allMembers.size(); i++) {
                SendMessage message = new SendMessage();
                message.setParseMode("HTML");
                message.setText(constructMember(allMembers.get(i), i));
                messages.add(message);
            }
        }
        catch (HttpClientErrorException e){
            SendMessage message = new SendMessage();
            message.setText("Something went wrong. Please, try again later: \n" +
                    e.getMessage());
            messages.add(message);
        }
        return messages;
    }
    public SendMessage getMemberByEmail(String email){
        SendMessage message = new SendMessage();
        try{
            message.setParseMode("HTML");
            message.setText(constructMember(parseJson(gitLabApiClient.getMemberByEmail(email)), 0));
        }
        catch (HttpClientErrorException.BadRequest e){
            message.setText("Wrong email. Please, try again.");
        }
        catch (HttpClientErrorException e){
            message.setText("Something went wrong. Please, try again later: \n" +
                    e.getMessage());
        }
        return message;
    }
    public List<SendMessage> getMRsAll(){
        List<SendMessage> messages = new ArrayList<>();
        try {
            JsonNode allMRs = parseJson(gitLabApiClient.getMRsAll());
            for (int i = 0; i < allMRs.size(); i++) {
                SendMessage message = new SendMessage();
                message.setParseMode("HTML");
                message.setText(constructMR(allMRs.get(i), i));
                messages.add(message);
            }
        }
        catch (HttpClientErrorException e){
            SendMessage message = new SendMessage();
            message.setText("Something went wrong. Please, try again later: \n" +
                    e.getMessage());
            messages.add(message);
        }
        return messages;
    }
    public SendMessage getMRByIid(int iid){
        SendMessage message = new SendMessage();
        try {
            message.setParseMode("HTML");
            message.setText(constructMR(parseJson(gitLabApiClient.getMRByIid(iid)), 0));
        }
        catch (HttpClientErrorException.BadRequest e){
            message.setText("Wrong merge request id. Please, try again.");
        }
        catch (HttpClientErrorException e){
            message.setText("Something went wrong. Please, try again later: \n" +
                    e.getMessage());
        }
        return message;
    }
    public List<SendMessage> getMRsByFilter(String email, OffsetDateTime since, OffsetDateTime until){
        List<SendMessage> messages = new ArrayList<>();
        try {
            JsonNode allMRs = parseJson(gitLabApiClient.getMRsByFilter(email, since, until));
            for (int i = 0; i < allMRs.size(); i++) {
                SendMessage message = new SendMessage();
                message.setParseMode("HTML");
                message.setText(constructMR(allMRs.get(i), i));
                messages.add(message);
            }
        }
        catch (IllegalArgumentException e){
            SendMessage message = new SendMessage();
            message.setText("At least one filter must be specified.");
            messages.add(message);
        }
        catch (HttpClientErrorException.BadRequest e){
            SendMessage message = new SendMessage();
            message.setText("Nothing to show.");
            messages.add(message);
        }
        catch (HttpClientErrorException e){
            SendMessage message = new SendMessage();
            message.setText("Something went wrong. Please, try again later: \n" +
                    e.getMessage());
            messages.add(message);
        }
        return messages;
    }
    public List<SendMessage> getBranchesAll(){
        List<SendMessage> messages = new ArrayList<>();
        try {
            JsonNode allBranches = parseJson(gitLabApiClient.getBranchesAll());
            for (int i = 0; i < allBranches.size(); i++) {
                SendMessage message = new SendMessage();
                message.setParseMode("HTML");
                message.setText(constructBranch(allBranches.get(i), i));
                messages.add(message);
            }
        }
        catch (HttpClientErrorException e){
            SendMessage message = new SendMessage();
            message.setText("Something went wrong. Please, try again later: \n" +
                    e.getMessage());
            messages.add(message);
        }
        return messages;
    }
    public SendMessage getBranchByName(String name){
        SendMessage message = new SendMessage();
        try {
            message.setParseMode("HTML");
            message.setText(constructBranch(parseJson(gitLabApiClient.getBranchByName(name)), 0));
        }
        catch (HttpClientErrorException.BadRequest e){
            message.setText("Wrong branch name. Please, try again.");
        }
        catch (HttpClientErrorException e){
            message.setText("Something went wrong. Please, try again later: \n" +
                    e.getMessage());
        }
        return message;
    }
}
