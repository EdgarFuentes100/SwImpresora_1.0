package print;

import api.ApiClient;
import api.model.Comanda;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class TicketHandler {

    private final PrinterService printerService;
    private final ApiClient apiClient;
    private final ObjectMapper mapper;

    public TicketHandler(PrinterService printerService, ApiClient apiClient) {
        this.printerService = printerService;
        this.apiClient = apiClient;
        this.mapper = new ObjectMapper();
    }

    public void handleMessage(String message) {
        try {
            Map<String, Object> data = mapper.readValue(message, Map.class);

            String grupo = ((String) data.getOrDefault("grupo", "default")).toLowerCase();
            String numeroTicket = String.valueOf(data.getOrDefault("numeroTicket", "0"));
            String tipo = (String) data.getOrDefault("tipo", "Desconocido");

            String impresora = apiClient.getImpresora(grupo);

            Comanda comanda = apiClient.getComandaObj(numeroTicket, tipo);

            String contenido = comanda.formatearTicket();

            printerService.imprimirTicket(grupo, impresora, contenido);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
