package print;

import auth.JWTService;
import ws.WSClient;
import api.ApiClient;  // Importa ApiClient

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PrintServiceInit {

    private WSClient wsClient;
    private TicketHandler ticketHandler;
    private final String logPath = "C:\\Users\\Fuentes\\Desktop\\INsta\\service.log";

    public void start() {
        log("Iniciando servicio de impresión...");

        try {
            // Instanciar los servicios necesarios
            PrinterService printerService = new PrinterService();
            ApiClient apiClient = new ApiClient();  // Crear la instancia de ApiClient
            ticketHandler = new TicketHandler(printerService, apiClient);  // Pasar ambos servicios al constructor de TicketHandler

            JWTService jwtService = new JWTService("MI_SECRETO_DE_EXE_QUE_ES_LO_SUFICIENTE_32_BYTES");
            wsClient = new WSClient("ws://localhost:8080/", jwtService, ticketHandler);

            wsClient.setOnMessage(msg -> {
                log("Mensaje recibido: " + msg);
                ticketHandler.handleMessage(msg);  // Llamada a handleMessage
            });

            // Hilo para intentar reconectar si la conexión se cae
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

            log("Servicio corriendo.");

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
}
