package print;

import auth.JWTService;
import ws.WSClient;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PrintServiceInit {

    private WSClient wsClient;
    private TicketHandler ticketHandler;
    private boolean modoPrueba;
    private final String logPath = "C:\\Users\\Fuentes\\Desktop\\INsta\\service.log"; // Ruta de log

    public PrintServiceInit(boolean modoPrueba) {
        this.modoPrueba = modoPrueba;
    }

    public void start() {
        log("Iniciando servicio de impresión...");

        try {
            PrinterService printerService = new PrinterService();
            ticketHandler = new TicketHandler(printerService);

            JWTService jwtService = new JWTService("MI_SECRETO_DE_EXE_QUE_ES_LO_SUFICIENTE_32_BYTES");
            wsClient = new WSClient("ws://localhost:8080/", jwtService, ticketHandler);

            // Configurar recepción de mensajes
            wsClient.setOnMessage(msg -> {
                log("Mensaje recibido: " + msg);
                ticketHandler.handleMessage(msg, modoPrueba);
            });

            // Reconexión automática
            new Thread(() -> {
                while (true) {
                    try {
                        if (!wsClient.isConnected()) {
                            log("Intentando reconectar WebSocket...");
                            wsClient.connect("EXE_1");
                        }
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        logError("Error en hilo de reconexión: ", e);
                    }
                }
            }, "ReconexionThread").start();

            log("Servicio corriendo. Modo prueba: " + modoPrueba);

        } catch (Exception e) {
            logError("Error al iniciar servicio: ", e);
        }
    }

    private void log(String mensaje) {
        try (FileWriter fw = new FileWriter(logPath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(mensaje);
        } catch (IOException ignored) {}
    }

    private void logError(String mensaje, Exception e) {
        try (FileWriter fw = new FileWriter(logPath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(mensaje);
            e.printStackTrace(pw);
        } catch (IOException ignored) {}
    }

    public void stop() {
        try {
            if (wsClient != null) wsClient.disconnect();
            log("Servicio detenido.");
        } catch (Exception e) {
            logError("Error al detener servicio: ", e);
        }
    }

    // Puedes eliminar este main si ya tienes uno externo
    /*
    public static void main(String[] args) {
        boolean modoPrueba = false;
        PrintServiceInit servicio = new PrintServiceInit(modoPrueba);
        servicio.start();

        synchronized (PrintServiceInit.class) {
            try {
                PrintServiceInit.class.wait();
            } catch (InterruptedException e) {
                servicio.logError("Hilo principal interrumpido: ", e);
            }
        }
    }
    */
}
