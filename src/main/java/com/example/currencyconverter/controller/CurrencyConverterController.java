package com.example.currencyconverter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;


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
            @RequestParam double amount,
            @RequestParam String from,
            @RequestParam String to) {

        double convertedAmount = getExchangeRate(from, to) * amount;

        Map<String, Object> result = new HashMap<>();
        result.put("amount", amount);
        result.put("from", from);
        result.put("to", to);
        result.put("convertedAmount", String.format("%.2f", convertedAmount));

        return result;
    }

    private double getExchangeRate(String from, String to) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = apiUrl + from + "?apikey=" + apiKey; // Use API to get rates for all currencies

        Map<String, Object> response = restTemplate.getForObject(requestUrl, Map.class);

        if (response != null && response.containsKey("rates")) {
            Map<String, Double> rates = (Map<String, Double>) response.get("rates");
            if (rates.containsKey(to)) {
                return rates.get(to); // Return the exchange rate for the requested currency
            }
        }

        return 1.0; // Default to 1.0 if exchange rate is not found
    }

}
