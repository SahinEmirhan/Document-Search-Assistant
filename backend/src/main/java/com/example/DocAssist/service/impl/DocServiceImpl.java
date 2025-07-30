package com.example.DocAssist.service.impl;

import com.example.DocAssist.dto.FindSimilarRequest;
import com.example.DocAssist.model.EmbeddedChunk;
import com.example.DocAssist.model.EmbeddedDoc;
import com.example.DocAssist.repository.DocRepository;
import com.example.DocAssist.repository.EmbeddedChunkRepository;
import com.example.DocAssist.service.IDocService;
import com.example.DocAssist.service.IEmbeddingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DocServiceImpl implements IDocService {

    private IEmbeddingService embeddingService;

    private DocRepository docRepository;

    private EmbeddedChunkRepository embeddedChunkRepository;

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;

    @Autowired
    public DocServiceImpl(IEmbeddingService embeddingService, DocRepository docRepository, EmbeddedChunkRepository embeddedChunkRepository) {
        this.embeddingService = embeddingService;
        this.docRepository = docRepository;
        this.embeddedChunkRepository = embeddedChunkRepository;
    }

    public String extractText(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();


        List<String> textList = new ArrayList<>();
        String[] textArr = stripper.getText(document).split("\\r?\\n\\r?\\n");
        if (textArr.length < 5) {
            textArr = stripper.getText(document).split("\\.");
        }
        int windowSize = 4;

        List<String> cleanedParagraphs = new ArrayList<>();
        for (String paragraph : textArr) {
            String trimmed = paragraph.trim();
            if (!trimmed.isEmpty()) {
                cleanedParagraphs.add(trimmed);
            }
        }
        for (int i = 0; i <= cleanedParagraphs.size() - windowSize; i++) {
            StringBuilder windowText = new StringBuilder();
            for (int j = i; j < i + windowSize; j++) {
                windowText.append(cleanedParagraphs.get(j));
                if (j < i + windowSize - 1) {
                    windowText.append("\n\n");
                }
            }
            textList.add(windowText.toString());
        }
        List<List<Double>> allEmbeddings = new ArrayList<>();
        int chunkSize = 512;

        for (int i = 0; i < textList.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, textList.size());
            List<String> subList = textList.subList(i, end);

            List<List<Double>> embeddingChunk = embeddingService.getEmbeddingFromText(subList);
            allEmbeddings.addAll(embeddingChunk);
        }
        List<EmbeddedChunk> embeddedChunkList = new ArrayList<>();
        IntStream.range(0, allEmbeddings.size())
                .forEach(i -> {
                    EmbeddedChunk chunk = new EmbeddedChunk();
                    chunk.setText(textList.get(i));
                    chunk.setChunk(allEmbeddings.get(i));
                    embeddedChunkList.add(embeddedChunkRepository.save(chunk));
                });

        EmbeddedDoc embeddedDoc = new EmbeddedDoc();
        embeddedDoc.setEmbeddedChunks(embeddedChunkList);
        embeddedDoc.setDocId(UUID.randomUUID().toString());
        docRepository.save(embeddedDoc);
        document.close();
        return embeddedDoc.getDocId();
    }


    public String getSimilarString(FindSimilarRequest request) throws JsonProcessingException {
        EmbeddedDoc dbDoc = docRepository.findByDocId(request.getDocId());
        if(dbDoc==null){
            throw new RuntimeException("Document not found");
        }
        List<List<Double>> embeddedQuery = embeddingService.getEmbeddingFromText(List.of(request.getQuery()));

        Map<Double , String> maxSimilars = new TreeMap<>();

        for(EmbeddedChunk embeddedChunk : dbDoc.getEmbeddedChunks()){
            Double similarity = embeddingService.cosineSimilarity(embeddedQuery.get(0), embeddedChunk.getChunk());
            maxSimilars.put(similarity , embeddedChunk.getText());
        }

        List<Map.Entry<Double, String>> lastThree = new ArrayList<>(((TreeMap<Double, String>) maxSimilars).descendingMap().entrySet())
                .subList(0, Math.min(5, maxSimilars.size()));

        String combinedText = lastThree.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.joining("\n\n"));

        return createChatBotMessage(combinedText , request.getQuery());
    }


    //this method using in getSimilarString method.
    public String createChatBotMessage(String message , String query) throws JsonProcessingException {
        String userMessage = "Lütfen sadece aşağıdaki soruya doğrudan cevap ver (soru dilinde), ekstra açıklama yapma.\n" +
                "Soru: " + query + "\n" +
                "İçerik: " + message;

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;

        Map<String, Object> part = new HashMap<>();
        part.put("text", userMessage);

        Map<String, Object> innerContent = new HashMap<>();
        innerContent.put("parts", List.of(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(innerContent));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);


        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        String text = root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        return text;
    }


}
