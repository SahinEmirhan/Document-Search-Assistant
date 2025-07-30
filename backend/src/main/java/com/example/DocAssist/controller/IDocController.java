package com.example.DocAssist.controller;

import com.example.DocAssist.dto.FindSimilarRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IDocController {
    ResponseEntity<String> extractText(MultipartFile file) throws IOException;
    ResponseEntity<String> getSimilarString(FindSimilarRequest request) throws JsonProcessingException;
}
