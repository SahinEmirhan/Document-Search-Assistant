package com.example.DocAssist.controller.impl;

import com.example.DocAssist.controller.IDocController;
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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/document")
public class DocControllerImpl implements IDocController {

    private IDocService docService;

    @Autowired
    public DocControllerImpl(IDocService docService) {
        this.docService = docService;
    }

    @Override
    @PostMapping("/extract")
    public ResponseEntity<String> extractText(@RequestBody MultipartFile file) throws IOException {
        return ResponseEntity.ok(docService.extractText(file));
    }


    @Override
    @PostMapping("/findSimilar")
    public ResponseEntity<String> getSimilarString(@RequestBody FindSimilarRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(docService.getSimilarString(request));
    }

}
