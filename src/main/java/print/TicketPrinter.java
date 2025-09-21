package print;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TicketPrinter {

    private final String printerName; // Opcional, null = impresora predeterminada

    public TicketPrinter() {
        this.printerName = null;
    }

    public TicketPrinter(String printerName) {
        this.printerName = printerName;
    }

    // Imprimir directamente en la impresora
    public void printTicket(String contenido) {
        try {
            File tempFile = File.createTempFile("ticket_", ".txt");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(contenido);
            }

            String command;
            if (printerName == null || printerName.isEmpty()) {
                command = "notepad /p \"" + tempFile.getAbsolutePath() + "\"";
            } else {
                command = "cmd /c print /D:\"" + printerName + "\" \"" + tempFile.getAbsolutePath() + "\"";
            }

            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            pb.start();

            tempFile.deleteOnExit();
            System.out.println("Ticket enviado a impresión: " + tempFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Abrir Notepad para ver el ticket
    public void abrirTicket(String contenido) {
        try {
            File tempFile = File.createTempFile("ticket_", ".txt");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(contenido);
            }

            // Usando ProcessBuilder para abrir Notepad confiablemente
            ProcessBuilder pb = new ProcessBuilder("notepad.exe", tempFile.getAbsolutePath());
            pb.start();

            tempFile.deleteOnExit();
            System.out.println("Ticket abierto en Notepad: " + tempFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
