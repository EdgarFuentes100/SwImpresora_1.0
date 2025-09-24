package print;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Desktop;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketPrinter {

    private final String printerName;
    private final boolean modoPrueba;

    public TicketPrinter(String printerName, boolean modoPrueba) {
        this.printerName = printerName;
        this.modoPrueba = modoPrueba;
    }

    public void printTicket(String contenido) {
        try {
            File backupFile = new File("tickets_backup");
            if (!backupFile.exists()) backupFile.mkdirs();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File file = new File(backupFile, "ticket_" + timestamp + ".txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(contenido);
            }

            if (modoPrueba || printerName == null || printerName.isEmpty()) {
                abrirTicketEditable(file);
            } else {
                imprimirDirecto(file);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void abrirTicketEditable(File file) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().edit(file);
        }
    }

    private void imprimirDirecto(File file) throws IOException, InterruptedException {
        // Supongamos que 'file' es el archivo PDF que deseas imprimir.
        // Asegúrate de que 'printerName' es el nombre correcto de la impresora.

        String printerCommand;

        if (printerName == null || printerName.isEmpty()) {
            // Si no se especifica la impresora, se usa la impresora predeterminada
            printerCommand = "cmd /c print /D:\"Microsoft Print to PDF\" \"" + file.getAbsolutePath() + "\"";
        } else {
            // Si tienes una impresora específica, usa su nombre.
            printerCommand = "cmd /c print /D:\"" + printerName + "\" \"" + file.getAbsolutePath() + "\"";
        }

        // Ejecutar el comando para imprimir
        Process process = Runtime.getRuntime().exec(printerCommand);
        process.waitFor();  // Esperar a que se termine el proceso de impresión
    }

}
