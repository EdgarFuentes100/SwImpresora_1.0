package service;

import api.ApiClient;
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
        return api.put("/grupoPrinter/actualizar/" + id, grupo);
    }
}
