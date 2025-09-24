package print;

public class PrinterService {

    public PrinterService() {
        // Constructor vacío está bien
    }

    // Método para imprimir el ticket usando impresora recibida o una de respaldo
    public void imprimirTicket(String grupo, String impresora, String contenido) {
        try {
            if (impresora == null || impresora.isEmpty()) {
                if ("grupo 1".equalsIgnoreCase(grupo)) {
                    impresora = "Microsoft Print to PDF";
                } else {
                    impresora = "Microsoft XPS Document Writer";
                }
            }

            TicketPrinter printer = new TicketPrinter(impresora);
            printer.printTicket(contenido);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
