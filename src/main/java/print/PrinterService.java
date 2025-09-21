package print;

import java.util.HashMap;
import java.util.Map;

public class PrinterService {

    private final Map<String, String> grupoImpresora;

    public PrinterService() {
        grupoImpresora = new HashMap<>();
        // Inicializa los grupos con impresora por defecto
        grupoImpresora.put("Cocina", "Impresora1");
        grupoImpresora.put("Bar", "Impresora2");
    }

    public void asignarImpresora(String grupo, String impresora) {
        grupoImpresora.put(grupo, impresora);
    }

    public String getPrinterForGroup(String grupo) {
        return grupoImpresora.get(grupo);
    }

    // Método centralizado de impresión
    public void imprimirTicket(String grupo, String contenido, boolean soloVer) {
        String impresora = getPrinterForGroup(grupo);
        TicketPrinter printer = new TicketPrinter(impresora);
        if (soloVer) {
            printer.abrirTicket(contenido); // abrir Notepad para ver
        } else {
            printer.printTicket(contenido); // imprimir directamente
        }
    }
}
