package gui;

import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrinterTestPanel extends JPanel {

    private JComboBox<String> comboTestImpresoras;

    public PrinterTestPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Prueba de Impresoras"));
        setBackground(new Color(245, 245, 245));
        setPreferredSize(new Dimension(280, 200));

        JLabel testLabel = new JLabel("Seleccionar impresora:");
        testLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        testLabel.setFont(new Font("Arial", Font.BOLD, 12));

        comboTestImpresoras = new JComboBox<>(obtenerImpresoras());
        comboTestImpresoras.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboTestImpresoras.setMaximumSize(new Dimension(260, 30));

        JButton enviarTestButton = UIUtils.createStyledButton("Enviar Test de Impresión", new Color(102, 0, 102));
        enviarTestButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        enviarTestButton.setMaximumSize(new Dimension(260, 35));
        enviarTestButton.addActionListener(e -> enviarTest());

        add(Box.createVerticalStrut(10));
        add(testLabel);
        add(Box.createVerticalStrut(8));
        add(comboTestImpresoras);
        add(Box.createVerticalStrut(20));
        add(enviarTestButton);
        add(Box.createVerticalGlue());
    }

    private String[] obtenerImpresoras() {
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        String[] nombres = new String[impresoras.length + 1];
        nombres[0] = "";
        for (int i = 0; i < impresoras.length; i++) {
            nombres[i + 1] = impresoras[i].getName();
        }
        return nombres;
    }

    private void enviarTest() {
        String impresoraSeleccionada = comboTestImpresoras.getSelectedItem().toString();
        if (impresoraSeleccionada.isEmpty()) return;

        JDialog testDialog = UIUtils.createLoadingDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Enviando test a impresora...");
        testDialog.setModalityType(Dialog.ModalityType.MODELESS);
        testDialog.setVisible(true);

        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8081/probar?impresora=" + URLEncoder.encode(impresoraSeleccionada, "UTF-8"));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);

                int responseCode = con.getResponseCode();
                java.io.InputStream is = (responseCode == 200) ? con.getInputStream() : con.getErrorStream();
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                s.close();

                SwingUtilities.invokeLater(() -> {
                    testDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                            "Impresora: " + impresoraSeleccionada +
                                    "\nCódigo de respuesta: " + responseCode +
                                    "\nRespuesta del servidor:\n" + response,
                            "Test de Impresión", JOptionPane.INFORMATION_MESSAGE);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    testDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                            "Error al enviar test a '" + impresoraSeleccionada + "':\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
                ex.printStackTrace();
            }
        }).start();
    }
}
