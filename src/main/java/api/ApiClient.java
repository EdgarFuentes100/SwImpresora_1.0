package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import api.model.ApiResponse;
import api.model.Comanda;
import api.model.Producto;
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
    public String getImpresora(String grupo) throws Exception {
        // Se asegura de que el grupo esté codificado correctamente en la URL
        String endpoint = "/api/v1/grupoPrinter/Impresora/" + grupo;
        String urlCompleta = baseUrl + endpoint;  // Combina la baseUrl con el endpoint

        // Imprime la URL para depuración, opcional
        System.out.println("URL completa para obtener impqwqwqwqwqwqwresora: " + urlCompleta);

        return get(endpoint);  // Llama al método GET con el endpoint relativo
    }


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

        ApiResponse response = mapper.readValue(json, ApiResponse.class);

        Comanda comanda = new Comanda();
        if (response.getDatos() != null && !response.getDatos().isEmpty()) {
            Producto primerProducto = response.getDatos().get(0);

            comanda.setNumeroTicket(numeroTicket);
            comanda.setMesa(primerProducto.getMesa());
            comanda.setCliente(primerProducto.getCliente());
            comanda.setMesero(primerProducto.getMesero());
            comanda.setEstado(response.getMessage());
            comanda.setProductos(response.getDatos());
        }
        return comanda;
    }

}
