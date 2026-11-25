package fr.adriencaubel.rag_with_spring;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RetrievalService {

    private final ChatClient chatClient;

    public RetrievalService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String ask(UUID conversationId, String question) {
        return chatClient.prompt()
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId.toString()))
                .call()
                .content();
    }
}
