package com.example.demo.entity;

import jakarta.persistence.Entity;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class FxRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String sourceCurrency;
    private String targetCurrency;
    private Double exchangeRate;
}