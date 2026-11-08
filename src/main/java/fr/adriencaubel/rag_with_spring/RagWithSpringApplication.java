package fr.adriencaubel.rag_with_spring;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RagWithSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagWithSpringApplication.class, args);
	}

	@Bean
	public SimpleVectorStore vectorStore(EmbeddingModel embeddingModel) {
		return SimpleVectorStore.builder(embeddingModel).build();
	}

	@Bean
	ChatClient defaultChatClient(ChatClient.Builder builder, VectorStore vectorStore) {
		return builder
				.defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
						.searchRequest(SearchRequest.builder()
								.topK(5)
								.similarityThreshold(0.5)
								.build())
						.promptTemplate(new PromptTemplate("""
								Contexte:
								---------------------
								{question_answer_context}
								---------------------

								Question: {query}

								Tu réponds UNIQUEMENT à partir du contexte fourni.
								Si le contexte ne contient pas la réponse, dis-le explicitement.
								Réponds en français."""))
						.build())
				.build();
	}
}
