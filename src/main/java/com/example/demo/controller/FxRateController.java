package com.example.demo.controller;

import com.example.demo.entity.FxRate;
import com.example.demo.service.FxRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fx")
public class FxRateController {

    @Autowired
    private FxRateService fxRateService;

    @GetMapping
    public FxRate getFxRate(@RequestParam String targetCurrency) {
        return fxRateService.getFxRate(targetCurrency);
    }

    @GetMapping("/{targetCurrency}")
    public List<FxRate> getLatestFxRates(@PathVariable String targetCurrency) {
        return fxRateService.getLatestFxRates(targetCurrency);
    }
}
