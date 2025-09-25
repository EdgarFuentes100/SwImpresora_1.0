import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.*;
import model.grupoPrinterModel;
import service.grupoPrinterService;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Gestión de Grupos de Impresoras");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1100, 600)); // Aumentado el ancho para el panel lateral
        frame.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // OBTENER IMPRESORAS (al inicio para usarlas en toda la interfaz)
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        String[] impresorasDisponibles = new String[impresoras.length + 1];
        impresorasDisponibles[0] = "";
        for (int i = 0; i < impresoras.length; i++) {
            impresorasDisponibles[i + 1] = impresoras[i].getName();
        }

        // PANEL IZQUIERDO (contenido principal)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        // PANEL DERECHO (test impresoras)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Prueba de Impresoras"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        rightPanel.setPreferredSize(new Dimension(280, 200));
        rightPanel.setBackground(new Color(245, 245, 245));

        JLabel testLabel = new JLabel("Seleccionar impresora:");
        testLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        testLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JComboBox<String> comboTestImpresoras = new JComboBox<>(impresorasDisponibles);
        comboTestImpresoras.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboTestImpresoras.setMaximumSize(new Dimension(260, 30));
        comboTestImpresoras.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton enviarTestButton = createStyledButton("Enviar Test de Impresión", new Color(102, 0, 102));
        enviarTestButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        enviarTestButton.setMaximumSize(new Dimension(260, 35));

        // Agregar componentes al panel derecho con espaciado
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(testLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(comboTestImpresoras);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(enviarTestButton);
        rightPanel.add(Box.createVerticalGlue());

        // PANEL SUPERIOR ORIGINAL (sin test)
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        JPanel titleInfoPanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Configuración de Grupos de Impresoras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel infoLabel = new JLabel("Total de grupos: 0 | Impresoras disponibles: 0");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.DARK_GRAY);
        titleInfoPanel.add(titleLabel);
        titleInfoPanel.add(infoLabel);
        JButton refreshButton = createStyledButton("Actualizar", new Color(150, 150, 150));
        headerPanel.add(titleInfoPanel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        leftPanel.add(headerPanel, BorderLayout.NORTH);

        // TABLA
        String[] columnNames = {"ID Grupo", "Nombre del Grupo", "Impresora Asignada", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Grupos"));
        scrollPane.setPreferredSize(new Dimension(800, 400));
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // COMBO PARA TABLA
        JComboBox<String> comboBoxImpresoras = new JComboBox<>(impresorasDisponibles);
        comboBoxImpresoras.setFont(new Font("Arial", Font.PLAIN, 12));
        TableColumn impresoraColumn = table.getColumnModel().getColumn(2);
        impresoraColumn.setCellEditor(new DefaultCellEditor(comboBoxImpresoras));

        // PANEL INFERIOR (solo guardar)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Guardar Cambios", new Color(0, 51, 102));
        controlPanel.add(saveButton);
        leftPanel.add(controlPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // CAMBIOS PENDIENTES
        Map<Integer, String> cambiosPendientes = new HashMap<>();
        model.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 2) {
                Integer idGrupo = Integer.parseInt(model.getValueAt(row, 0).toString());
                String nuevaImpresora = model.getValueAt(row, 2).toString();
                cambiosPendientes.put(idGrupo, nuevaImpresora);
                model.setValueAt(nuevaImpresora.isEmpty() ? "No asignada" : "Asignada", row, 3);
            }
        });

        // ACCIÓN REFRESH
        refreshButton.addActionListener(e -> {
            cargarDatos(table, model, impresorasDisponibles, infoLabel);
            cambiosPendientes.clear();
        });

        // ACCIÓN GUARDAR
        saveButton.addActionListener(e -> {
            if (cambiosPendientes.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No hay cambios para guardar.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, "¿Deseas guardar los cambios realizados?", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            grupoPrinterService service = new grupoPrinterService();
            List<String> errores = new ArrayList<>();
            for (Map.Entry<Integer, String> cambio : cambiosPendientes.entrySet()) {
                int id = cambio.getKey();
                String impresora = cambio.getValue();
                grupoPrinterModel g = new grupoPrinterModel();
                g.setImpresora(impresora);
                try {
                    String respuesta = service.actualizarGrupo(id, g);
                    if (!respuesta.startsWith("Actualización exitosa")) {
                        errores.add("Grupo " + id + ": " + respuesta);
                    }
                } catch (Exception ex) {
                    errores.add("Grupo " + id + ": " + ex.getMessage());
                }
            }

            if (errores.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Cambios guardados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cambiosPendientes.clear();
                cargarDatos(table, model, impresorasDisponibles, infoLabel);
            } else {
                JOptionPane.showMessageDialog(frame, "Errores:\n" + String.join("\n", errores), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ACCIÓN ENVIAR TEST (modificada para el panel lateral)
// ACCIÓN ENVIAR TEST
        enviarTestButton.addActionListener(e -> {
            String impresoraSeleccionada = comboTestImpresoras.getSelectedItem().toString();
            if (impresoraSeleccionada.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Selecciona una impresora para enviar el test.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Crear diálogo de carga NO modal
            JDialog testDialog = createLoadingDialog(frame, "Enviando test a impresora...");
            testDialog.setModalityType(Dialog.ModalityType.MODELESS); // Evita bloquear el hilo de la GUI
            testDialog.setVisible(true);

            // Hilo para enviar el test
            new Thread(() -> {
                try {
                    java.net.URL url = new java.net.URL(
                            "http://localhost:8081/probar?impresora=" + java.net.URLEncoder.encode(impresoraSeleccionada, "UTF-8")
                    );
                    java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.setConnectTimeout(10000);
                    con.setReadTimeout(10000);

                    int responseCode = con.getResponseCode();
                    java.io.InputStream is = (responseCode == 200) ? con.getInputStream() : con.getErrorStream();
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : "";
                    s.close();

                    // Actualizar GUI desde el hilo de Swing
                    SwingUtilities.invokeLater(() -> {
                        testDialog.setVisible(false); // cerrar diálogo de carga
                        testDialog.dispose();
                        JOptionPane.showMessageDialog(frame,
                                "Impresora: " + impresoraSeleccionada +
                                        "\nCódigo de respuesta: " + responseCode +
                                        "\nRespuesta del servidor:\n" + response,
                                "Test de Impresión", JOptionPane.INFORMATION_MESSAGE);
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        testDialog.setVisible(false);
                        testDialog.dispose();
                        JOptionPane.showMessageDialog(frame,
                                "Error al enviar test a '" + impresoraSeleccionada + "':\n" + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    });
                    ex.printStackTrace();
                }
            }).start();
        });

        // CARGAR DATOS INICIALES
        cargarDatos(table, model, impresorasDisponibles, infoLabel);
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static void cargarDatos(JTable table, DefaultTableModel model, String[] impresorasDisponibles, JLabel infoLabel) {
        model.setRowCount(0);
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(table);
        JDialog loadingDialog = createLoadingDialog(parentFrame, "Cargando datos...");

        new Thread(() -> {
            try {
                grupoPrinterService service = new grupoPrinterService();
                List<grupoPrinterModel> grupoList = service.obtenerGrupo();

                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    int gruposConImpresora = 0;
                    for (grupoPrinterModel grupo : grupoList) {
                        String impresora = grupo.getImpresora();
                        String estado = "No asignada";
                        String seleccion = "";
                        for (String imp : impresorasDisponibles) {
                            if (imp.equals(impresora)) {
                                seleccion = imp;
                                estado = "Asignada";
                                gruposConImpresora++;
                                break;
                            }
                        }
                        model.addRow(new Object[]{grupo.getIdGrupoPrinter(), grupo.getNombre(), seleccion, estado});
                    }
                    infoLabel.setText(String.format(
                            "Total de grupos: %d | Grupos con impresora: %d | Impresoras disponibles: %d",
                            grupoList.size(), gruposConImpresora, impresorasDisponibles.length - 1
                    ));
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    JOptionPane.showMessageDialog(parentFrame,
                            "Error al cargar los datos:\n" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
                e.printStackTrace();
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    private static JDialog createLoadingDialog(JFrame parent, String message) {
        JDialog dialog = new JDialog(parent, "Cargando", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(label, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        dialog.add(panel);

        return dialog;
    }
}