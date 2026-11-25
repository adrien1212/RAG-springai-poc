package fr.adriencaubel.rag_with_spring.controller;

import fr.adriencaubel.rag_with_spring.model.Conversation;
import fr.adriencaubel.rag_with_spring.repository.ConversationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ConversationController {

    public record ConversationResponse(UUID id, String title) {}

    private final ConversationRepository conversationRepository;

    public ConversationController(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse create() {
        // No authentication yet: each conversation gets a random owner identifier
        Conversation conversation = conversationRepository.save(
                new Conversation(UUID.randomUUID().toString(), "New conversation"));
        return new ConversationResponse(conversation.getId(), conversation.getTitle());
    }
}
