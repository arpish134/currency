package com.example.demo.service;


import com.example.demo.entity.FxRate;
import com.example.demo.repository.FxRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FxRateService {

    @Autowired
    private FxRateRepository fxRateRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String ECB_API_URL = "https://api.frankfurter.app/latest?from=USD";

    public FxRate getFxRate(String targetCurrency) {
        LocalDate today = LocalDate.now();
        if (!fxRateRepository.existsByDateAndTargetCurrency(today, targetCurrency)) {
            fetchAndStoreRates();
        }
        return fxRateRepository.findByTargetCurrencyOrderByDateDesc(targetCurrency).get(0);
    }

    public List<FxRate> getLatestFxRates(String targetCurrency) {
        return fxRateRepository.findByTargetCurrencyOrderByDateDesc(targetCurrency)
                .stream()
                .limit(3)
                .collect(Collectors.toList());
    }

    private void fetchAndStoreRates() {
        Map<String, Map<String, Double>> response = restTemplate.getForObject(ECB_API_URL, Map.class);
        LocalDate date = LocalDate.now();

        for (Map.Entry<String, Double> entry : response.get("rates").entrySet()) {
            FxRate fxRate = new FxRate();
            fxRate.setDate(date);
            fxRate.setSourceCurrency("USD");
            fxRate.setTargetCurrency(entry.getKey());
            fxRate.setExchangeRate(entry.getValue());
            fxRateRepository.save(fxRate);
        }
    }
}
