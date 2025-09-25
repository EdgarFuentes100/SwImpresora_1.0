package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import api.model.ApiResponse;
import api.model.Comanda;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiClient {

    private final String baseUrl = "http://localhost:4000"; // URL base del backend
    private final HttpClient client;
    private final ObjectMapper mapper;

    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    // Método genérico GET
    private String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + endpoint))
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Obtener impresora por grupo
    public String getImpresora(String grupo) throws Exception {
        String endpoint = "/api/v1/grupoPrinter/Impresora/" + grupo;
        String json = get(endpoint);

        // Parsear solo el nombre de la impresora
        JsonNode node = new ObjectMapper().readTree(json);
        return node.path("impresora").asText();  // Devuelve "Microsoft Print to PDF"
    }

    // Obtener comanda completa o parcial
    public Comanda getComandaObj(String numeroTicket, String tipoComanda) throws Exception {
        String endpoint;

        if ("completa".equalsIgnoreCase(tipoComanda)) {
            endpoint = "/api/v1/impresion/listaImpresionCompleta/" + numeroTicket;
        } else if ("parcial".equalsIgnoreCase(tipoComanda)) {
            endpoint = "/api/v1/impresion/parcial/" + numeroTicket;
        } else {
            throw new IllegalArgumentException("Tipo de comanda no válido: " + tipoComanda);
        }

        String json = get(endpoint);
        System.out.println("JSON recibido para comanda: " + json);

        // Mapea la respuesta a ApiResponse
        ApiResponse response = mapper.readValue(json, ApiResponse.class);

        if (response.isOk() && response.getDatos() != null) {
            return response.getDatos(); // devuelve Comanda directamente
        } else {
            throw new Exception("No se encontró la comanda o hubo error: " + response.getMessage());
        }
    }
}
