package com.management.jupiter.repository;

import com.management.jupiter.services.CellServices;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IaRepo {

    private static final Pattern TEXT_PATTERN =
            Pattern.compile("\"text\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");

    public static List<String> useIA() {
        return useIA(4, "planetas");
    }

    public static List<String> useIA(int totalCells, String theme) {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
            String apiKey = "";
            if (apiKey == null || apiKey.isBlank()) {
                apiKey = dotenv.get("GEMINI_API_KEY");
            }
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException("Configura GEMINI_API_KEY antes de usar la IA.");
            }

            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + apiKey;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = """
                    {
                      "contents": [{
                        "parts":[{"text": "Genera exactamente %d nombres de células con temática de %s. Los nombres deben ser canónicos, reales y comúnmente aceptados dentro de esa temática. No inventes palabras. No uses diminutivos. No uses sufijos creativos. No combines palabras. No agregues descripciones. Devuelve solo una lista en una sola línea, separada por comas."}]
                      }]
                    }
                    """.formatted(totalCells, theme);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            InputStream is;

            int status = conn.getResponseCode();

            if (status >= 200 && status < 300) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            if (status < 200 || status >= 300) {
                throw new IllegalStateException("Error al llamar la IA: " + response);
            }

            return parseNamesFromResponse(response.toString());

        } catch (Exception e) {
            throw new RuntimeException("No fue posible obtener nombres desde la IA.", e);
        }
    }

    public static void createCellsFromIA(int clanId, int totalCells, String theme) {
        List<String> names = useIA(totalCells, theme);
        CellServices cellServices = new CellServices();

        for (String name : names) {
            cellServices.createCell(name, clanId);
        }
    }

    public static List<String> parseNamesFromResponse(String response) {
        Matcher matcher = TEXT_PATTERN.matcher(response);
        if (!matcher.find()) {
            throw new IllegalStateException("No se pudo extraer el texto de la respuesta de la IA.");
        }

        String text = matcher.group(1)
                .replace("\\n", " ")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .trim();

        String[] rawNames = text.split(",");
        List<String> names = new ArrayList<>();
        for (String rawName : rawNames) {
            String cleaned = rawName.trim()
                    .replaceAll("^[0-9]+[.)-]?\\s*", "") // delete 1. 1- 1) 1 in the start text
                    .replaceAll("^[-*]\\s*", "") //delete al spaces in the start
                    .trim();
            if (!cleaned.isEmpty()) {
                names.add(cleaned);
            }
        }

        if (names.isEmpty()) {
            throw new IllegalStateException("La IA respondió, pero no devolvió nombres válidos.");
        }

        return names;
    }
}
