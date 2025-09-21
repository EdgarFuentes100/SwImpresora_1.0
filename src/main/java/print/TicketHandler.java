package print;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class TicketHandler {

    private final PrinterService printerService;
    private final ObjectMapper mapper;

    public TicketHandler(PrinterService printerService) {
        this.printerService = printerService;
        this.mapper = new ObjectMapper();
    }

    // Método principal con opción de ver o imprimir
    public void handleMessage(String message, boolean soloVer) {
        try {
            Map<String, Object> data = mapper.readValue(message, Map.class);

            String grupo = (String) data.getOrDefault("grupo", "SinGrupo");
            String numeroTicket = String.valueOf(data.getOrDefault("numeroTicket", "0"));
            String tipo = (String) data.getOrDefault("tipo", "Desconocido");

            String contenido = "------ TICKET ------\n" +
                    "Número: " + numeroTicket + "\n" +
                    "Tipo: " + tipo + "\n" +
                    "Grupo: " + grupo + "\n" +
                    "-------------------\n";

            printerService.imprimirTicket(grupo, contenido, soloVer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sobrecarga para compatibilidad con llamadas antiguas
    public void handleMessage(String message) {
        handleMessage(message, false); // Por defecto imprime directo
    }
}
