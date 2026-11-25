package fr.adriencaubel.rag_with_spring.repository;

import fr.adriencaubel.rag_with_spring.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
