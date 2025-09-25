package print;

public class PrinterService {

    public PrinterService() {
        // Constructor vacío
    }

    public void imprimirTicket(String impresora, String contenido, String contenidoPlano) {
        try {
            if (impresora == null || impresora.isEmpty()) {
                // Si no se pasa impresora, usa la predeterminada del sistema
                System.out.println("No se especificó impresora, usando la predeterminada.");
                impresora = null;
            }

            TicketPrinter printer = new TicketPrinter(impresora);
            printer.printTicket(contenido, contenidoPlano);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
