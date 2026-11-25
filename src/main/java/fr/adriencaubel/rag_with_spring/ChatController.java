package fr.adriencaubel.rag_with_spring;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ChatController {

    public record ChatRequest(UUID conversationId, String question) {}

    private final RetrievalService retrievalService;

    public ChatController(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return retrievalService.ask(request.conversationId(), request.question());
    }
}
