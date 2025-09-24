package ws;

import auth.JWTService;
import print.TicketHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class WSClient {

    private final String serverUrl;
    private final JWTService jwtService;
    private final TicketHandler ticketHandler;
    private WebSocketClient client;
    private Consumer<String> onMessageCallback;
    private boolean conectado = false;

    public WSClient(String serverUrl, JWTService jwtService, TicketHandler ticketHandler) {
        this.serverUrl = serverUrl;
        this.jwtService = jwtService;
        this.ticketHandler = ticketHandler;
    }

    public void setOnMessage(Consumer<String> callback) {
        this.onMessageCallback = callback;
    }

    public boolean isConnected() {
        return conectado;
    }

    public void connect(String idExe) {
        try {
            String token = jwtService.generarToken(idExe);
            String url = serverUrl + "?token=" + token;

            client = new WebSocketClient(new URI(url)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    conectado = true;
                    System.out.println("✅ Conectado al backend Node.js");
                }

                @Override
                public void onMessage(String message) {
                    if (onMessageCallback != null) {
                        onMessageCallback.accept(message);
                    } else {
                        ticketHandler.handleMessage(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    conectado = false;
                    System.out.println("❌ Conexión cerrada: " + reason);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            WSClient.this.connect(idExe);
                        }
                    }, 2000);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("⚠️ Error WebSocket: " + ex.getMessage());
                }
            };

            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (client != null && !client.isClosed()) {
                client.close();
                conectado = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
