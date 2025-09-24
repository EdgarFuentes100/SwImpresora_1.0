package print;

import java.util.HashMap;
import java.util.Map;

public class PrinterService {

    private final Map<String, String> grupoImpresora;

    public PrinterService() {
        grupoImpresora = new HashMap<>();
        grupoImpresora.put("Cocina", "Impresora1");
        grupoImpresora.put("Bar", "Impresora2");
    }

    public void asignarImpresora(String grupo, String impresora) {
        grupoImpresora.put(grupo, impresora);
    }

    public String getPrinterForGroup(String grupo) {
        return grupoImpresora.get(grupo);
    }

    public void imprimirTicket(String grupo, String contenido, boolean modoPrueba) {
        String impresora = getPrinterForGroup(grupo);
        TicketPrinter printer = new TicketPrinter(impresora, modoPrueba);
        printer.printTicket(contenido);
    }
}
