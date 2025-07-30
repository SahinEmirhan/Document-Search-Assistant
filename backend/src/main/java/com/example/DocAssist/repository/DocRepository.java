package com.example.DocAssist.repository;

import com.example.DocAssist.model.EmbeddedChunk;
import com.example.DocAssist.model.EmbeddedDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocRepository extends JpaRepository<EmbeddedDoc, Long> {
    EmbeddedDoc findByDocId(String docId);
}
