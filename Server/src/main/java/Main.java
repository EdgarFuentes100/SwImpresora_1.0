import print.PrintServiceInit;

public class Main {
    public static void main(String[] args) {
        PrintServiceInit servicio = new PrintServiceInit();
        servicio.start();

        synchronized (Main.class) {
            try {
                Main.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
