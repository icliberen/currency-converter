package com.example.currencyconverter.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CurrencyConverterController {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    @GetMapping("/convert")
    public Map<String, Object> convertCurrency(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Double amount) {

        // ✅ Error Handling: Check if any parameter is missing
        if (from == null || to == null || amount == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Missing parameters! Please provide 'from', 'to', and 'amount'.");
            return errorResponse;
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL + from;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("rates")) {
            Map<String, Double> rates = (Map<String, Double>) response.get("rates");
            if (rates.containsKey(to)) {
                double convertedAmount = amount * rates.get(to);

                Map<String, Object> result = new HashMap<>();
                result.put("amount", amount);
                result.put("from", from);
                result.put("to", to);
                result.put("convertedAmount", String.format("%.2f", convertedAmount));
                return result;
            }
        }

        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("error", "Invalid conversion request");
        return errorResult;
    }
}
