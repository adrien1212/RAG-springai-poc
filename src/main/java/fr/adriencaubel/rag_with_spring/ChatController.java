package fr.adriencaubel.rag_with_spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final RetrievalService retrievalService;

    public ChatController(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @GetMapping("/chat")
    public String ask(@RequestParam String question) {
        return retrievalService.ask(question);
    }
}
