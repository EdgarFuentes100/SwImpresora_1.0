import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import model.grupoPrinterModel;
import service.grupoPrinterService;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Grupos de Impresoras");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 300);
            frame.setLayout(new BorderLayout());

            // Columnas de la tabla
            String[] columnNames = {"ID Grupo", "Nombre", "Impresora"};

            // Modelo de la tabla (impedir edición por defecto)
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Solo permitir edición en la columna de impresoras (columna 2)
                    return column == 2;
                }
            };

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Obtener impresoras instaladas en Windows
            PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
            String[] impresorasDisponibles = new String[impresoras.length];
            for (int i = 0; i < impresoras.length; i++) {
                impresorasDisponibles[i] = impresoras[i].getName();
            }

            // ComboBox con impresoras disponibles
            JComboBox<String> comboBoxImpresoras = new JComboBox<>(impresorasDisponibles);
            TableColumn impresoraColumn = table.getColumnModel().getColumn(2);
            impresoraColumn.setCellEditor(new DefaultCellEditor(comboBoxImpresoras));

            // Llamar al servicio para obtener los datos
            new Thread(() -> {
                grupoPrinterService service = new grupoPrinterService();
                try {
                    List<grupoPrinterModel> grupoList = service.obtenerGrupo();

                    SwingUtilities.invokeLater(() -> {
                        for (grupoPrinterModel grupo : grupoList) {
                            String impresoraDelGrupo = grupo.getImpresora();

                            // Verificar si existe en las impresoras instaladas
                            String seleccion = null;
                            for (String imp : impresorasDisponibles) {
                                if (imp.equalsIgnoreCase(impresoraDelGrupo)) {
                                    seleccion = imp;
                                    break;
                                }
                            }

                            // Si no coincide, dejar en blanco (para que usuario seleccione)
                            model.addRow(new Object[]{
                                    grupo.getIdGrupoPrinter(),
                                    grupo.getNombre(),
                                    seleccion != null ? seleccion : "" // Combo vacío si no existe
                            });
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame,
                            "Error al obtener grupo de impresoras:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }).start();

            frame.setVisible(true);
        });
    }
}
