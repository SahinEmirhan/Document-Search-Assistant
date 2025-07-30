package com.example.DocAssist.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface IEmbeddingService {
    List<List<Double>> getEmbeddingFromText(List<String> textList) throws JsonProcessingException;
    double cosineSimilarity(List<Double> vecA, List<Double> vecB);
}
