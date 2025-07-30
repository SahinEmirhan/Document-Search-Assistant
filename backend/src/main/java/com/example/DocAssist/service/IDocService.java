package com.example.DocAssist.service;

import com.example.DocAssist.dto.FindSimilarRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IDocService {
    String extractText(MultipartFile file) throws IOException;
    String getSimilarString(FindSimilarRequest request) throws JsonProcessingException;
    String createChatBotMessage(String message , String query) throws JsonProcessingException;
}
