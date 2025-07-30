package com.example.DocAssist.repository;

import com.example.DocAssist.model.EmbeddedChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmbeddedChunkRepository extends JpaRepository<EmbeddedChunk, Long> {
}
