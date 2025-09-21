package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiClient {

    private final String baseUrl = "http://localhost:4000"; // <-- URL base fija
    private final HttpClient client;
    private final ObjectMapper mapper;

    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    private String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + endpoint))
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String post(String endpoint, Object data) throws Exception {
        String json = mapper.writeValueAsString(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Métodos específicos
    public String getProductos() throws Exception {
        return get("/api/v1/producto/ListaProducto");
    }

    public String postImprimir(Object data) throws Exception {
        return post("/api/v1/imprimir", data);
    }
}
