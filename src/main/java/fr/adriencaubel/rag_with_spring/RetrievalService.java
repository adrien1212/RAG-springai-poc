package fr.adriencaubel.rag_with_spring;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class RetrievalService {

    private final ChatClient chatClient;

    public RetrievalService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String ask(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
