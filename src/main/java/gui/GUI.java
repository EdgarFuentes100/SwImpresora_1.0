package gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class GUI extends JFrame {

    private JPanel panel;
    private Map<String, JComboBox<String>> grupoImpresoras;

    public GUI(String[] grupos) {
        setTitle("Asignación de impresoras por grupo");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));

        grupoImpresoras = new HashMap<>();
        String[] impresoras = getImpresorasDisponibles();

        for (String grupo : grupos) {
            JLabel label = new JLabel(grupo);
            JComboBox<String> combo = new JComboBox<>(impresoras);
            grupoImpresoras.put(grupo, combo);

            panel.add(label);
            panel.add(combo);
        }

        add(panel);
        setVisible(true);
    }

    public String getImpresora(String grupo) {
        JComboBox<String> combo = grupoImpresoras.get(grupo);
        if (combo != null) return (String) combo.getSelectedItem();
        return null;
    }

    private String[] getImpresorasDisponibles() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        String[] nombres = new String[services.length];
        for (int i = 0; i < services.length; i++) {
            nombres[i] = services[i].getName();
        }
        return nombres;
    }
}
