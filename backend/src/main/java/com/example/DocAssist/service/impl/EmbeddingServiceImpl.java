package com.example.DocAssist.service.impl;


import com.example.DocAssist.service.IEmbeddingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmbeddingServiceImpl implements IEmbeddingService {


    @Value("${jina.api.key}")
    private String JINA_API_KEY;
    private final String EMBEDDING_ENDPOINT = "https://api.jina.ai/v1/embeddings";

    public List<List<Double>> getEmbeddingFromText(List<String> textList) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", JINA_API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "jina-embeddings-v4");
        requestBody.put("task", "text-matching");

        List<Map<String, String>> inputList = new ArrayList<>();

        for(String text : textList){
            Map<String, String> textMap = new HashMap<>();
            textMap.put("text", text);
            inputList.add(textMap);
        }
        requestBody.put("input", inputList);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                EMBEDDING_ENDPOINT,
                HttpMethod.POST,
                httpEntity,
                Map.class
        );


        // Response içinden embedding değerini çıkar
        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");
        List<List<Double>> embeddings = data.stream()
                .map(item -> (List<Double>) item.get("embedding"))
                .collect(Collectors.toList());


        return embeddings;
    }


    public double cosineSimilarity(List<Double> vecA, List<Double> vecB) {
        if (vecA.size() != vecB.size()) {
            throw new IllegalArgumentException("Vector sizes do not match");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vecA.size(); i++) {
            double a = vecA.get(i);
            double b = vecB.get(i);
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        if (normA == 0 || normB == 0) {
            return 0; // ya da exception, vektör boşsa
        }

        return dotProduct / (normA * normB);
    }
}
