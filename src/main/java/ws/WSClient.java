package ws;

import auth.JWTService;
import print.PrinterService;
import print.TicketHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class WSClient {

    private final String serverUrl;
    private final JWTService jwtService;
    private final TicketHandler ticketHandler;
    private WebSocketClient client;

    public WSClient(String serverUrl, JWTService jwtService, TicketHandler ticketHandler) {
        this.serverUrl = serverUrl;
        this.jwtService = jwtService;
        this.ticketHandler = ticketHandler;
    }

    public void connect(String idExe) {
        try {
            String token = jwtService.generarToken(idExe);
            String url = serverUrl + "?token=" + token;

            client = new WebSocketClient(new URI(url)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("✅ Conectado al backend Node.js");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("📩 Mensaje recibido: " + message);
                    // Siempre usa impresora por defecto
                    ticketHandler.handleMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("❌ Conexión cerrada: " + reason);
                    System.out.println("🔄 Reconectando en 2 segundos...");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            WSClient.this.connect(idExe);
                        }
                    }, 2000);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("⚠️ Error: " + ex.getMessage());
                }
            };

            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
