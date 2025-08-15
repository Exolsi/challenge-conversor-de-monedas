import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

public class ConversorMonedas {

    // API key inline (tal como la compartiste)
    private static final String API_KEY_INLINE = "Introducir aqui su API KEY";

    // Endpoint base (tu ejemplo: https://v6.exchangerate-api.com/v6/<KEY>/latest/USD)
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    // Monedas permitidas en el challenge
    private static final Set<String> MONEDAS_PERMITIDAS =
            new HashSet<>(Arrays.asList("USD", "ARS", "BOB"));

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1) Obtenemos la API key (inline en este caso)
        String apiKey = API_KEY_INLINE;
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("No se proporcionó API key. Saliendo...");
            return;
        }

        // 2) Pedimos monedas
        String from = pedirCodigo(sc, "Moneda ORIGEN (USD, ARS o BOB): ");
        String to   = pedirCodigo(sc, "Moneda DESTINO (USD, ARS o BOB): ");

        // 3) Monto
        double amount = pedirMonto(sc, "Monto a convertir (ej: 100.0): ");

        // 4) Consultar API: /latest/{FROM}
        String url = BASE_URL + apiKey + "/latest/" + from;
        String body;
        try {
            body = httpGet(url);
        } catch (Exception e) {
            System.out.println("Error al consultar la API: " + e.getMessage());
            return;
        }

        // 5) Parsear y validar JSON
        JsonObject root = JsonParser.parseString(body).getAsJsonObject();
        if (!root.has("result") || !"success".equals(root.get("result").getAsString())) {
            System.out.println("La API retornó un error:");
            System.out.println(body);
            return;
        }

        JsonObject rates = root.getAsJsonObject("conversion_rates");
        if (!rates.has(to)) {
            System.out.println("No hay tasa para " + to + " en conversion_rates.");
            return;
        }

        double rate = rates.get(to).getAsDouble();
        double converted = amount * rate;

        System.out.printf(Locale.US, "%,.4f %s = %,.4f %s%n", amount, from, converted, to);
        System.out.println("Base: " + root.get("base_code").getAsString());
        System.out.println("Última actualización: " + root.get("time_last_update_utc").getAsString());
    }

    // --- Utilidades ---

    private static String pedirCodigo(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String code = sc.nextLine().trim().toUpperCase();
            if (MONEDAS_PERMITIDAS.contains(code)) return code;
            System.out.println("Moneda no permitida. Solo: " + MONEDAS_PERMITIDAS);
        }
    }

    private static double pedirMonto(Scanner sc, String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String txt = sc.nextLine().trim().replace(',', '.');
                return Double.parseDouble(txt);
            } catch (NumberFormatException e) {
                System.out.println("Monto inválido. Intenta de nuevo (ej: 150.75).");
            }
        }
    }

    private static String httpGet(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException(
                    "HTTP " + response.statusCode() + " al consultar " + url + "\n" + response.body()
            );
        }
        return response.body();
    }
}
