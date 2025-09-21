import auth.JWTService;
import print.PrinterService;
import print.TicketHandler;
import ws.WSClient;
import javax.swing.SwingUtilities;

public class Main {

    private static final String SECRET_EXE = "MI_SECRETO_DE_EXE_QUE_ES_LO_SUFICIENTE_32_BYTES";
    private static final String ID_EXE = "EXE_1";
    private static final String SERVER_URL = "ws://localhost:8080/";

    public static void main(String[] args) {

        PrinterService printerService = new PrinterService(); // impresoras por defecto
        TicketHandler ticketHandler = new TicketHandler(printerService);
        JWTService jwtService = new JWTService(SECRET_EXE);

        // 1️⃣ Abrir la GUI en Swing
        SwingUtilities.invokeLater(() -> {
            String[] grupos = {"Cocina", "Bar"};
            gui.GUI gui = new gui.GUI(grupos);
        });

        // 2️⃣ Conectar al WebSocket en el hilo principal
        WSClient wsClient = new WSClient(SERVER_URL, jwtService, ticketHandler);
        wsClient.connect(ID_EXE);

        // Mantener corriendo
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
