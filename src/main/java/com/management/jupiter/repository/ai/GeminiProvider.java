package com.management.jupiter.repository.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GeminiProvider implements AiProvider {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String API_KEY = dotenv.get("API_GEMINI");
    private final String ENDPOINT =
            dotenv.get("ENDPOINT_GEMINI") + API_KEY;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<String> generateNames(int total, String theme) {
        try {

            String jsonInput = """
                    {
                      "contents": [{
                        "parts":[{"text": "Genera exactamente %d nombres ALEATORIOS de células con temática de %s. Los nombres deben ser canónicos, reales y comúnmente aceptados dentro de esa temática. No inventes palabras. No uses diminutivos. No uses sufijos creativos. No combines palabras. No agregues descripciones. Devuelve solo una lista en una sola línea, separada por comas."}]
                      }]
                    }
                    """.formatted(total, theme);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error API: " + response.body());
            }

            return parseResponse(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Error usando Gemini" + e.getMessage(), e);
        }
    }

    private List<String> parseResponse(String json) throws Exception {
        JsonNode root = mapper.readTree(json);

        String text = root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        String[] rawNames = text.split(",");
        List<String> names = new ArrayList<>();

        for (String raw : rawNames) {
            String clean = raw.trim()
                    .replaceAll("^[0-9]+[.)-]?\\s*", "")
                    .replaceAll("^[-*]\\s*", "");

            if (!clean.isEmpty()) {
                names.add(clean);
            }
        }

        return names;
    }
}
