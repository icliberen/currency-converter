package com.example.currencyconverter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CurrencyConverterController {

    @Value("${exchange.api.key}") // API key from application.properties
    private String apiKey;

    @Value("${exchange.api.url}") // API URL from application.properties
    private String apiUrl;

    @GetMapping("/convert")
    public Map<String, Object> convertCurrency(
            @RequestParam BigDecimal amount, // Using BigDecimal
            @RequestParam String from,
            @RequestParam String to) {

        BigDecimal exchangeRate = getExchangeRate(from, to);
        BigDecimal convertedAmount = amount.multiply(exchangeRate); // Use multiply() instead of `*`

        Map<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("from", from);
        result.put("to", to);
        result.put("convertedAmount", convertedAmount.setScale(2, BigDecimal.ROUND_HALF_UP)); // Round to 2 decimal places

        return result;
    }

    private BigDecimal getExchangeRate(String from, String to) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = apiUrl + from + "?apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(requestUrl, Map.class);

        if (response != null && response.containsKey("rates")) {
            Map<String, Object> rates = (Map<String, Object>) response.get("rates");
            if (rates.containsKey(to)) {
                return new BigDecimal(rates.get(to).toString()); // Convert rate to BigDecimal
            }
        }
        return BigDecimal.ONE; // Default to 1.0 if rate is missing
    }

}
