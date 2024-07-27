package com.example.demo.repository;


import com.example.demo.entity.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FxRateRepository extends JpaRepository<FxRate, Long> {
    List<FxRate> findByTargetCurrencyOrderByDateDesc(String targetCurrency);
    boolean existsByDateAndTargetCurrency(LocalDate date, String targetCurrency);
}
