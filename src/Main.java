//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
package app;

import com.google.gson.*;
import java.net.http.*;
import java.net.URI;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final String API_KEY = System.getenv("EXCHANGE_RATE_API_KEY"); // setéala en tu SO
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private static final String[] SUPPORTED = {"USD","ARS","BOB","BRL","CLP","COP"};

    public static void main(String[] args) throws Exception {
        if (API_KEY == null || API_KEY.isBlank()) {
            System.err.println("Falta la variable de entorno EXCHANGE_RATE_API_KEY");
            System.exit(1);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("=== Challenge Conversor de Monedas ===");
        System.out.println("Códigos soportados: USD, ARS, BOB, BRL, CLP, COP");

        System.out.print("Moneda ORIGEN (ej: USD): ");
        String from = sc.nextLine().trim().toUpperCase();
        System.out.print("Moneda DESTINO (ej: ARS): ");
        String to   = sc.nextLine().trim().toUpperCase();
        System.out.print("Monto (ej: 100.0): ");
        double amount = Double.parseDouble(sc.nextLine().trim());

        // Validación simple
        if (!isSupported(from) || !isSupported(to)) {
            System.out.println("Código no soportado en este demo.");
            return;
        }

        // Llamada al endpoint estándar: /latest/{FROM}
        String url = BASE_URL + API_KEY + "/latest/" + from;
        HttpClient http = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() / 100 != 2) {
            System.out.println("HTTP " + res.statusCode() + " al consultar " + url);
            return;
        }

        JsonObject root = JsonParser.parseString(res.body()).getAsJsonObject();
        if (!"success".equals(root.get("result").getAsString())) {
            System.out.println("API error: " + res.body());
            return;
        }

        JsonObject rates = root.getAsJsonObject("conversion_rates");
        if (!rates.has(to)) {
            System.out.println("No hay tasa para " + to);
            return;
        }

        double rate = rates.get(to).getAsDouble();
        double converted = amount * rate;

        System.out.printf("%f %s = %f %s%n", amount, from, converted, to);
        System.out.println("Última actualización: " + root.get("time_last_update_utc").getAsString());
    }

    private static boolean isSupported(String code) {
        for (String c : SUPPORTED) if (c.equalsIgnoreCase(code)) return true;
        return false;
    }
}
