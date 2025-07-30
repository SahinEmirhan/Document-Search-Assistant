package com.example.DocAssist.model;

import com.example.DocAssist.dto.DoubleListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class EmbeddedChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = DoubleListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Double> chunk;

    @Column(length = 10000)
    private String text;

}
