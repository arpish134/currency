package com.example.demo;

import com.example.demo.entity.FxRate;
import com.example.demo.repository.FxRateRepository;
import com.example.demo.service.FxRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FxRateServiceTest {

    @Mock
    private FxRateRepository fxRateRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FxRateService fxRateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFxRateWhenDataExists() {
        LocalDate today = LocalDate.now();
        FxRate fxRate = new FxRate();
        fxRate.setDate(today);
        fxRate.setSourceCurrency("USD");
        fxRate.setTargetCurrency("EUR");
        fxRate.setExchangeRate(0.85);

        when(fxRateRepository.existsByDateAndTargetCurrency(today, "EUR")).thenReturn(true);
        when(fxRateRepository.findByTargetCurrencyOrderByDateDesc("EUR")).thenReturn(Collections.singletonList(fxRate));

        FxRate result = fxRateService.getFxRate("EUR");

        assertNotNull(result);
        assertEquals("EUR", result.getTargetCurrency());
        assertEquals(0.85, result.getExchangeRate());

        verify(fxRateRepository, times(1)).existsByDateAndTargetCurrency(today, "EUR");
        verify(fxRateRepository, times(1)).findByTargetCurrencyOrderByDateDesc("EUR");
        verify(restTemplate, never()).getForObject(anyString(), eq(Map.class));
    }

    @Test
    public void testGetFxRateWhenDataDoesNotExist() {
        LocalDate today = LocalDate.now();
        FxRate fxRate = new FxRate();
        fxRate.setDate(today);
        fxRate.setSourceCurrency("USD");
        fxRate.setTargetCurrency("EUR");
        fxRate.setExchangeRate(0.85);

        Map<String, Map<String, Double>> mockApiResponse = Map.of(
                "rates", Map.of("EUR", 0.85)
        );

        when(fxRateRepository.existsByDateAndTargetCurrency(today, "EUR")).thenReturn(false);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockApiResponse);
        when(fxRateRepository.save(any(FxRate.class))).thenReturn(fxRate);
        when(fxRateRepository.findByTargetCurrencyOrderByDateDesc("EUR")).thenReturn(Collections.singletonList(fxRate));

        FxRate result = fxRateService.getFxRate("EUR");

        assertNotNull(result);
        assertEquals("EUR", result.getTargetCurrency());
        assertEquals(0.85, result.getExchangeRate());

        verify(fxRateRepository, times(1)).existsByDateAndTargetCurrency(today, "EUR");
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Map.class));
        verify(fxRateRepository, times(1)).save(any(FxRate.class));
        verify(fxRateRepository, times(1)).findByTargetCurrencyOrderByDateDesc("EUR");
    }

    @Test
    public void testGetLatestFxRates() {
        FxRate fxRate1 = new FxRate();
        fxRate1.setDate(LocalDate.now());
        fxRate1.setSourceCurrency("USD");
        fxRate1.setTargetCurrency("EUR");
        fxRate1.setExchangeRate(0.85);

        FxRate fxRate2 = new FxRate();
        fxRate2.setDate(LocalDate.now().minusDays(1));
        fxRate2.setSourceCurrency("USD");
        fxRate2.setTargetCurrency("EUR");
        fxRate2.setExchangeRate(0.84);

        FxRate fxRate3 = new FxRate();
        fxRate3.setDate(LocalDate.now().minusDays(2));
        fxRate3.setSourceCurrency("USD");
        fxRate3.setTargetCurrency("EUR");
        fxRate3.setExchangeRate(0.83);

        FxRate fxRate4 = new FxRate();
        fxRate4.setDate(LocalDate.now().minusDays(3));
        fxRate4.setSourceCurrency("USD");
        fxRate4.setTargetCurrency("EUR");
        fxRate4.setExchangeRate(0.82);

        List<FxRate> fxRates = Arrays.asList(fxRate1, fxRate2, fxRate3, fxRate4);

        when(fxRateRepository.findByTargetCurrencyOrderByDateDesc("EUR")).thenReturn(fxRates);

        List<FxRate> result = fxRateService.getLatestFxRates("EUR");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(0.85, result.get(0).getExchangeRate());
        assertEquals(0.84, result.get(1).getExchangeRate());
        assertEquals(0.83, result.get(2).getExchangeRate());

        verify(fxRateRepository, times(1)).findByTargetCurrencyOrderByDateDesc("EUR");
    }
}
