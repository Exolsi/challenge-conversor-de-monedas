import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Challenge Conversor de Monedas
 * - Historial de conversiones (últimas N)
 * - Más monedas soportadas
 * - Registros con marca de tiempo (java.time)
 */
public class ConversorMonedas {

    // === Configuración ===
    // ⚠️ Si subes a GitHub público, NO dejes tu API key aquí en claro.
    private static final String API_KEY = "aa1d3bbfd592b5def2124800";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    // Monedas permitidas (puedes agregar/quitar aquí)
    private static final Set<String> MONEDAS_PERMITIDAS = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    static {
        // Del enunciado
        MONEDAS_PERMITIDAS.addAll(Arrays.asList("USD","ARS","BOB","BRL","CLP","COP"));
        // Ampliación
        MONEDAS_PERMITIDAS.addAll(Arrays.asList("EUR","MXN","PEN","PYG","UYU","GBP","CAD","JPY","CHF"));
    }

    // Historial (cap N)
    private static final int HISTORY_LIMIT = 20;
    private static final Deque<Conversion> HISTORIAL = new ArrayDeque<>(HISTORY_LIMIT);

    // Formato fecha/hora
    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    // ====== Programa principal ======
    public static void main(String[] args) {
        if (API_KEY == null || API_KEY.isBlank()) {
            System.out.println("No se configuró la API key. Saliendo…");
            return;
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            mostrarMenu();
            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> realizarConversion(sc);
                case "2" -> mostrarHistorial();
                case "3" -> listarMonedas();
                case "0" -> {
                    System.out.println("¡Hasta luego!");
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
            System.out.println();
        }
    }

    // ====== Lógica de negocio ======

    private static void realizarConversion(Scanner sc) {
        System.out.println("Monedas soportadas: " + String.join(", ", MONEDAS_PERMITIDAS));

        String from = pedirCodigo(sc, "Moneda ORIGEN: ");
        String to   = pedirCodigo(sc, "Moneda DESTINO: ");
        double amount = pedirMonto(sc, "Monto a convertir: ");

        // Llamada a la API /latest/{FROM}
        String url = BASE_URL + API_KEY + "/latest/" + from;
        String body;
        try {
            body = httpGet(url);
        } catch (Exception e) {
            System.out.println("Error HTTP: " + e.getMessage());
            return;
        }

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

        String stamp = ZonedDateTime.now(ZoneId.systemDefault()).format(TS_FMT);
        System.out.printf(Locale.US, "%,.4f %s = %,.4f %s%n", amount, from, converted, to);
        System.out.println("Tasa: " + rate + " | Base: " + root.get("base_code").getAsString());
        System.out.println("Última actualización (API): " + root.get("time_last_update_utc").getAsString());
        System.out.println("Registro local: " + stamp);

        // Guardar en historial (cap)
        pushHistorial(new Conversion(stamp, from, to, amount, rate, converted));
    }

    private static void mostrarHistorial() {
        if (HISTORIAL.isEmpty()) {
            System.out.println("No hay conversiones en el historial.");
            return;
        }
        System.out.println("=== Historial (últimas " + HISTORY_LIMIT + ") ===");
        int i = 1;
        for (Conversion c : HISTORIAL) {
            System.out.printf(
                    Locale.US,
                    "%2d) [%s] %,.4f %s -> %,.4f %s  (tasa: %,.6f)%n",
                    i++, c.timestamp, c.amount, c.from, c.converted, c.to, c.rate
            );
        }
    }

    private static void listarMonedas() {
        System.out.println("=== Monedas disponibles (" + MONEDAS_PERMITIDAS.size() + ") ===");
        int count = 0;
        StringBuilder row = new StringBuilder();
        for (String code : MONEDAS_PERMITIDAS) {
            row.append(code).append(' ');
            count++;
            if (count % 12 == 0) {
                System.out.println(row.toString().trim());
                row.setLength(0);
            }
        }
        if (row.length() > 0) System.out.println(row.toString().trim());
    }

    // ====== Helpers ======

    private static void mostrarMenu() {
        System.out.println("""
                ===== Challenge Conversor de Monedas =====
                1) Realizar conversión
                2) Ver historial
                3) Listar monedas disponibles
                0) Salir
                Selecciona una opción: """);
    }

    private static String pedirCodigo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String code = sc.nextLine().trim().toUpperCase(Locale.ROOT);
            if (MONEDAS_PERMITIDAS.contains(code)) return code;
            System.out.println("Código no soportado. Usa: " + String.join(", ", MONEDAS_PERMITIDAS));
        }
    }

    private static double pedirMonto(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String txt = sc.nextLine().trim().replace(',', '.');
                return Double.parseDouble(txt);
            } catch (NumberFormatException e) {
                System.out.println("Monto inválido. Ejemplo válido: 150.75");
            }
        }
    }

    private static String httpGet(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response =
                HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException(
                    "HTTP " + response.statusCode() + " al consultar " + url + "\n" + response.body()
            );
        }
        return response.body();
    }

    private static void pushHistorial(Conversion c) {
        // Mantener tamaño máximo
        if (HISTORIAL.size() == HISTORY_LIMIT) {
            HISTORIAL.removeFirst();
        }
        HISTORIAL.addLast(c);
    }

    // POJO de historial
    private record Conversion(
            String timestamp,  // yyyy-MM-dd HH:mm:ss z (zona local)
            String from,
            String to,
            double amount,
            double rate,
            double converted
    ) {}
}
