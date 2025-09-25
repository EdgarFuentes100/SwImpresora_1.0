import print.PrintServiceInit;

public class Main {
    public static void main(String[] args) {

        // Silenciar logs de Jetty antes de iniciar Spark
        System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");

        PrintServiceInit servicio = new PrintServiceInit();
        servicio.start();

        ws.WstestGui wsServer = new ws.WstestGui();
        wsServer.iniciarHttpLocal(); // inicia Spark
        System.out.println("Servidor de prueba iniciado en http://localhost:8081");

        synchronized (Main.class) {
            try {
                Main.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
