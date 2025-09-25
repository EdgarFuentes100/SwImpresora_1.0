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
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Gestión de Grupos de Impresoras");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 500));
        frame.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // PANEL SUPERIOR: título y botón actualizar
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));

        JPanel titleInfoPanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Configuración de Grupos de Impresoras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel infoLabel = new JLabel("Total de grupos: 0 | Impresoras disponibles: 0");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.DARK_GRAY);

        titleInfoPanel.add(titleLabel);
        titleInfoPanel.add(infoLabel);

        JButton refreshButton = createStyledButton("Actualizar", new Color(150, 150, 150)); // Gris
        headerPanel.add(titleInfoPanel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabla
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
        scrollPane.setPreferredSize(new Dimension(800, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botón guardar
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Guardar Cambios", new Color(0, 51, 102)); // Azul oscuro
        controlPanel.add(saveButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Obtener impresoras disponibles
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        String[] impresorasDisponibles = new String[impresoras.length + 1];
        impresorasDisponibles[0] = "";
        for (int i = 0; i < impresoras.length; i++) {
            impresorasDisponibles[i + 1] = impresoras[i].getName();
        }

        // ComboBox para la columna "Impresora Asignada"
        JComboBox<String> comboBoxImpresoras = new JComboBox<>(impresorasDisponibles);
        comboBoxImpresoras.setFont(new Font("Arial", Font.PLAIN, 12));
        TableColumn impresoraColumn = table.getColumnModel().getColumn(2);
        impresoraColumn.setCellEditor(new DefaultCellEditor(comboBoxImpresoras));

        // Cambios pendientes
        Map<Integer, String> cambiosPendientes = new HashMap<>();

        // Detectar cambios en la tabla
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

        // Acción botón actualizar
        refreshButton.addActionListener(e -> {
            cargarDatos(table, model, impresorasDisponibles, infoLabel);
            cambiosPendientes.clear();
        });

        // Acción botón guardar
        saveButton.addActionListener(e -> {
            if (cambiosPendientes.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No hay cambios para guardar.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Imprimir cambios por consola antes de guardar
            System.out.println(">>> Cambios pendientes:");
            cambiosPendientes.forEach((id, imp) ->
                    System.out.println("Grupo ID " + id + " -> Nueva impresora: " + (imp.isEmpty() ? "[Sin impresora]" : imp))
            );

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
                    if (respuesta.startsWith("Actualización exitosa")) {
                        // Éxito
                    } else {
                        errores.add("Error al actualizar grupo " + id + ": " + respuesta);
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
                JOptionPane.showMessageDialog(frame, "Errores al guardar:\n" + String.join("\n", errores), "Errores", JOptionPane.ERROR_MESSAGE);
            }
        });

        cargarDatos(table, model, impresorasDisponibles, infoLabel);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private static void cargarDatos(JTable table, DefaultTableModel model,
                                    String[] impresorasDisponibles, JLabel infoLabel) {
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

                        model.addRow(new Object[]{
                                grupo.getIdGrupoPrinter(),
                                grupo.getNombre(),
                                seleccion,
                                estado
                        });
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 12));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(label, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setResizable(false);

        return dialog;
    }
}
