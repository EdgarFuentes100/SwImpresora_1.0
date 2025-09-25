package print;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.print.*;

public class TicketPrinter {

    private final String printerName;

    public TicketPrinter(String printerName) {
        this.printerName = printerName;
    }

    public void printTicket(String contenidoTermal, String contenidoPlano) {
        try {
            // Guardar el ticket plano
            guardarTicketPlano(contenidoPlano);

            // Guardar la impresión cruda
            guardarImpresionTermal(contenidoTermal);

            // Imprimir directamente en la impresora
            imprimirDirecto(contenidoTermal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarTicketPlano(String contenidoPlano) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File ticketsFolder = new File("tickets_backup");
            if (!ticketsFolder.exists()) ticketsFolder.mkdirs();

            File ticketFile = new File(ticketsFolder, "ticket_plano_" + timestamp + ".txt");
            try (FileWriter writer = new FileWriter(ticketFile, StandardCharsets.UTF_8)) {
                writer.write(contenidoPlano);
            }
            System.out.println("Ticket plano guardado en: " + ticketFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void guardarImpresionTermal(String contenidoTermal) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File impresionesFolder = new File("tickets_impresion");
            if (!impresionesFolder.exists()) impresionesFolder.mkdirs();

            File rawFile = new File(impresionesFolder, "impresion_termal_" + timestamp + ".bin");
            try (FileOutputStream fos = new FileOutputStream(rawFile)) {
                fos.write(contenidoTermal.getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("Impresión termal guardada en: " + rawFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void imprimirDirecto(String contenidoTermal) {
        try {
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService selectedPrinter = null;

            for (PrintService service : services) {
                if (printerName != null && printerName.equalsIgnoreCase(service.getName())) {
                    selectedPrinter = service;
                    break;
                }
            }

            if (selectedPrinter == null) {
                System.out.println("No se encontró la impresora '" + printerName + "'. Usando impresora por defecto.");
                selectedPrinter = PrintServiceLookup.lookupDefaultPrintService();
            }

            if (selectedPrinter == null) {
                System.err.println("No hay impresora disponible.");
                return;
            }

            DocPrintJob job = selectedPrinter.createPrintJob();
            byte[] bytes = contenidoTermal.getBytes(StandardCharsets.UTF_8);
            Doc doc = new SimpleDoc(bytes, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);

            System.out.println("Ticket termal enviado a la impresora: " + selectedPrinter.getName());

        } catch (PrintException e) {
            e.printStackTrace();
        }
    }
}
