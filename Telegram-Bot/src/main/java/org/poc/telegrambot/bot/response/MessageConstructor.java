package org.poc.telegrambot.bot.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poc.telegrambot.client.GitLabApiClient;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Iterator;
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

    public List<SendMessage> getMembersAll(){
        JsonNode allMembers = parseJson(gitLabApiClient.getMembersAll());
        List<SendMessage> messages = new ArrayList<>();
        for (int i = 0; i < allMembers.size(); i++) {
            JsonNode member = allMembers.get(i);
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
            String messageText = "#" + (i + 1) +
                    "\n\nid:    \t<a href=\"" + member.get("webUrl").asText() + "\">" + member.get("id").asLong() + "</a> " +
                    "\nusername:    \t" + member.get("username") +
                    "\nemail:    \t" + member.get("email") +
                    "\naccessLevel:    \t" + accessLevel +
                    "\nMerge Requests Created:    \t" + member.get("createdMRs").asInt();

            SendMessage message = new SendMessage();
            message.setParseMode("HTML");
            message.setText(messageText);
            messages.add(message);
        }
        return messages;
    }
}
