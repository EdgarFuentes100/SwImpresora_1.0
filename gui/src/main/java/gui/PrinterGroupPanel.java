package gui;

import model.grupoPrinterModel;
import service.grupoPrinterService;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PrinterGroupPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JLabel infoLabel;
    private Map<Integer, String> cambiosPendientes = new HashMap<>();
    private String[] impresorasDisponibles;

    public PrinterGroupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Obtener impresoras
        PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        impresorasDisponibles = new String[impresoras.length + 1];
        impresorasDisponibles[0] = "";
        for (int i = 0; i < impresoras.length; i++) {
            impresorasDisponibles[i + 1] = impresoras[i].getName();
        }

        // Panel superior
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        JPanel titleInfoPanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Configuración de Grupos de Impresoras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoLabel = new JLabel("Total de grupos: 0 | Impresoras disponibles: 0");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.DARK_GRAY);
        titleInfoPanel.add(titleLabel);
        titleInfoPanel.add(infoLabel);

        JButton refreshButton = UIUtils.createStyledButton("Actualizar", new Color(150, 150, 150));
        refreshButton.addActionListener(e -> {
            cargarDatos();
            cambiosPendientes.clear();
        });

        headerPanel.add(titleInfoPanel, BorderLayout.CENTER);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnNames = {"ID Grupo", "Nombre del Grupo", "Impresora Asignada", "Estado"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Grupos"));
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Combo para editar impresoras en la tabla
        JComboBox<String> comboBoxImpresoras = new JComboBox<>(impresorasDisponibles);
        TableColumn impresoraColumn = table.getColumnModel().getColumn(2);
        impresoraColumn.setCellEditor(new DefaultCellEditor(comboBoxImpresoras));

        // Listener para cambios
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

        // Panel inferior con botón guardar
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = UIUtils.createStyledButton("Guardar Cambios", new Color(0, 51, 102));
        saveButton.addActionListener(e -> guardarCambios());
        controlPanel.add(saveButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarDatos();
    }

    private void cargarDatos() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog loadingDialog = UIUtils.createLoadingDialog(parentFrame, "Cargando datos...");

        new Thread(() -> {
            try {
                grupoPrinterService service = new grupoPrinterService();
                List<grupoPrinterModel> grupoList = service.obtenerGrupo();

                SwingUtilities.invokeLater(() -> {
                    loadingDialog.setVisible(false);
                    model.setRowCount(0);
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
                    JOptionPane.showMessageDialog(parentFrame, "Error al cargar los datos:\n" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
                e.printStackTrace();
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    private void guardarCambios() {
        if (cambiosPendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay cambios para guardar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Deseas guardar los cambios realizados?", "Confirmación", JOptionPane.YES_NO_OPTION);
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
            JOptionPane.showMessageDialog(this, "Cambios guardados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cambiosPendientes.clear();
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, "Errores:\n" + String.join("\n", errores), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
