package ui;

import javax.swing.*;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;

public class ImpresorasGUI extends JFrame {

    public ImpresorasGUI() {
        setTitle("Lista de Impresoras");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel label = new JLabel("Selecciona una impresora:");
        add(label);

        // Obtener impresoras
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);

        // Llenar nombres
        String[] nombresImpresoras = new String[impresoras.length];
        for (int i = 0; i < impresoras.length; i++) {
            nombresImpresoras[i] = impresoras[i].getName();
        }

        JComboBox<String> comboBox = new JComboBox<>(nombresImpresoras);
        add(comboBox);
    }

    public static void main(String[] args) {
        // Llamar a la clase de la GUI desde el main
        SwingUtilities.invokeLater(() -> {
            ImpresorasGUI gui = new ImpresorasGUI();
            gui.setVisible(true);
        });
    }
}
