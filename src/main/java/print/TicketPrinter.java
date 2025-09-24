package print;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketPrinter {

    private final String printerName;

    // Constructor que solo recibe el nombre de la impresora
    public TicketPrinter(String printerName) {
        this.printerName = printerName;
    }

    // Método para imprimir el ticket
    public void printTicket(String contenido) {
        try {
            // Crear carpeta de respaldo si no existe
            File backupFolder = new File("tickets_backup");
            if (!backupFolder.exists()) backupFolder.mkdirs();

            // Crear el archivo de ticket con timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File file = new File(backupFolder, "ticket_" + timestamp + ".txt");

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(contenido);
            }

            // Imprimir directamente
            imprimirDirecto(file);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método para imprimir directamente en la impresora especificada
    private void imprimirDirecto(File file) throws IOException, InterruptedException {
        String printerCommand;

        // Si no hay nombre de impresora, usa una impresora predeterminada
        if (printerName == null || printerName.isEmpty()) {
            printerCommand = "cmd /c print /D:\"Microsoft Print to PDF\" \"" + file.getAbsolutePath() + "\"";
        } else {
            printerCommand = "cmd /c print /D:\"" + printerName + "\" \"" + file.getAbsolutePath() + "\"";
        }

        // Ejecutar el comando para imprimir
        Process process = Runtime.getRuntime().exec(printerCommand);
        process.waitFor();  // Esperar a que termine el proceso
    }
}
