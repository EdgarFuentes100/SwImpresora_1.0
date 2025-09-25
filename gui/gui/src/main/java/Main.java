import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Lista de Impresoras");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("Selecciona una impresora:");
        frame.add(label);

        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        String[] nombres = new String[impresoras.length];
        for (int i = 0; i < impresoras.length; i++) {
            nombres[i] = impresoras[i].getName();
        }

        JComboBox<String> combo = new JComboBox<>(nombres);
        frame.add(combo);

        frame.setVisible(true);
    }
}
