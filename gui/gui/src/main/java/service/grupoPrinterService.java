package service;

import api.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import model.GrupoPrinterResponse;
import model.grupoPrinterModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class grupoPrinterService {

    private final ApiClient api;
    private final ObjectMapper mapper;

    public grupoPrinterService() {
        this.api = new ApiClient();
        this.mapper = new ObjectMapper();
    }

    // Retornar solo la lista de grupoPrinterModel
    public List<grupoPrinterModel> obtenerGrupo() throws Exception {
        String getResponse = api.get("/grupoPrinter/ListaGrupoPrinter");

        // Parsear el JSON y extraer solo data
        GrupoPrinterResponse response = mapper.readValue(getResponse, GrupoPrinterResponse.class);

        return response.data; // Aquí solo regresas los datos
    }
    public String actualizarGrupo(int id, grupoPrinterModel grupo) throws Exception {
        String responseJson = api.put("/grupoPrinter/actualizar/" + id, grupo);

        // Puedes parsear el JSON (usando Jackson u otra librería)
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseJson);


        boolean success = rootNode.path("success").asBoolean(false);
        String message = rootNode.path("message").asText("No hay mensaje");
        System.out.println("Respuesta del servidor: " + responseJson);

        if (success) {
            return "Actualización exitosa: " + message;
        } else {
            return "Error al actualizar: " + message;
        }

    }

}
