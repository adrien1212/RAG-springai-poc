package fr.adriencaubel.rag_with_spring;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Run on startup
 */
@Component
public class IndexorService implements ApplicationRunner {

    @Value("classpath:docs/*")
    private Resource[] docs;

    private final VectorStore vectorStore;

    public IndexorService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Document> documents = new ArrayList<>();
        for (Resource doc : docs) {
            documents.addAll(new TikaDocumentReader(doc).read());
        }

        System.out.println(documents.size());

        DocumentTransformer splitter = TokenTextSplitter.builder().build();
        List<Document> chunks = splitter.apply(documents);

        // Under the hood, the store calls EmbeddingModel.embed() for each document (or in batches), attaches the vectors, and persists them along with metadata.
        vectorStore.add(chunks);
    }
}
