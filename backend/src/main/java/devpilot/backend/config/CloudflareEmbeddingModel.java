package devpilot.backend.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Primary
public class CloudflareEmbeddingModel implements EmbeddingModel {

    private static final int BATCH_SIZE = 50;

    private final RestClient client;
    private final String model;

    public CloudflareEmbeddingModel(
            @Value("${CF_ACCOUNT_ID}") String accountId,
            @Value("${CF_API_TOKEN}") String token,
            @Value("${cloudflare.embedding.model:@cf/baai/bge-m3}") String model) {
        this.model = model;
        this.client = RestClient.builder()
                .baseUrl("https://api.cloudflare.com/client/v4/accounts/" + accountId + "/ai/v1")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<String> texts = request.getInstructions();
        List<Embedding> out = new ArrayList<>();

        for (int start = 0; start < texts.size(); start += BATCH_SIZE) {
            List<String> batch = texts.subList(start, Math.min(start + BATCH_SIZE, texts.size()));

            Map<String, Object> res = client.post()
                    .uri("/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("model", model, "input", batch))
                    .retrieve()
                    .body(Map.class);

            List<Map<String, Object>> data = (List<Map<String, Object>>) res.get("data");
            for (Map<String, Object> item : data) {
                List<Number> vec = (List<Number>) item.get("embedding");
                float[] f = new float[vec.size()];
                for (int i = 0; i < f.length; i++) {
                    f[i] = vec.get(i).floatValue();
                }
                int index = ((Number) item.get("index")).intValue();
                out.add(new Embedding(f, start + index));
            }
        }
        return new EmbeddingResponse(out);
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }

    @Override
    public int dimensions() {
        return 1024;
    }
}