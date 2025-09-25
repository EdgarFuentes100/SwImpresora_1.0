package service;

import api.ApiClient;
import model.grupoPrinterModel;
public class grupoPrinterService {

    private final ApiClient api;

    public grupoPrinterService() {
        this.api = new ApiClient();
    }

    public String obtenerGrupo() throws Exception {
        return api.get("/grupoPrinter/ListaGrupoPrinter");
    }

    public String actualizarGrupo(int id, grupoPrinterModel grupo) throws Exception {
        return api.put("/grupoPrinter/actualizar/" + id, grupo);
    }
}
