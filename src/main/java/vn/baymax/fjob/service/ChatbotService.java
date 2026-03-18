package vn.baymax.fjob.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    private final ChatClient chatClient;

    public ChatbotService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String question) {

        return chatClient
                .prompt()
                .system("""
                        You are a job search assistant.
                        Use available tools to search job information from the database.
                        Always use tools when possible.
                            """)
                .user(question)
                .call()
                .content();
    }
}