package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Gestión de Grupos de Impresoras");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1100, 600));
        setLayout(new BorderLayout(10, 10));

        PrinterGroupPanel groupPanel = new PrinterGroupPanel();
        PrinterTestPanel testPanel = new PrinterTestPanel();

        add(groupPanel, BorderLayout.CENTER);
        add(testPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }
}
