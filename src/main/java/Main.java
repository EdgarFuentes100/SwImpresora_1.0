import print.PrintServiceInit;

public class Main {
    public static void main(String[] args) {
        boolean modoPrueba = false;
        PrintServiceInit servicio = new PrintServiceInit(modoPrueba);
        servicio.start();

        // Mantener el servicio vivo indefinidamente
        synchronized (Main.class) {
            try {
                Main.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
