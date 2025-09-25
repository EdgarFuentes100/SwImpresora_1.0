package api.model;

import java.util.List;

public class Comanda {
    private Encabezado encabezado;
    private String estado;
    private List<Producto> productos;

    // Getters y Setters
    public Encabezado getEncabezado() { return encabezado; }
    public void setEncabezado(Encabezado encabezado) { this.encabezado = encabezado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    // Formateo de ticket
    public String formatearTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TICKET ").append(encabezado.getNumeroTicket()).append(" ===\n")
                .append("Cliente: ").append(encabezado.getCliente()).append("\n")
                .append("Mesero: ").append(encabezado.getMesero()).append("\n")
                .append("Mesa: ").append(encabezado.getMesa()).append("\n")
                .append("Salón: ").append(encabezado.getSalon()).append("\n")
                .append("Fecha: ").append(encabezado.getFechaHora()).append("\n")
                .append("Grupo: ").append(encabezado.getGrupo()).append("\n")
                .append("------------------------------------\n");

        for (Producto p : productos) {
            sb.append(p.getCantidad()).append(" x ").append(p.getProducto()).append("\n");
        }

        sb.append("------------------------------------\n")
                .append("Grupo: ").append(encabezado.getGrupo()).append("\n"); // <-- Grupo abajo

        return sb.toString();
    }


    public String formatearTicketTermico() {
        StringBuilder sb = new StringBuilder();

        // Encabezado centrado y en negrita (si la impresora soporta ESC/POS)
        sb.append("\u001B\u0041\u0001") // ESC + A + 1 → centrar
                .append("\u001B\u0045\u0001") // ESC + E + 1 → negrita ON
                .append("TICKET ").append(encabezado.getNumeroTicket()).append("\n")
                .append("\u001B\u0045\u0000"); // negrita OFF

        // Información general
        sb.append("Cliente: ").append(encabezado.getCliente()).append("\n")
                .append("Mesero: ").append(encabezado.getMesero()).append("\n")
                .append("Mesa: ").append(encabezado.getMesa()).append("\n")
                .append("Salón: ").append(encabezado.getSalon()).append("\n")
                .append("Fecha: ").append(encabezado.getFechaHora()).append("\n")
                .append("Grupo: ").append(encabezado.getGrupo()).append("\n")
                .append("--------------------------------\n");

        // Productos
        for (Producto p : productos) {
            // Puedes alinear cantidad a la derecha
            sb.append(String.format("%-20s %3d\n", p.getProducto(), p.getCantidad()));
        }

        sb.append("--------------------------------\n")
                .append("Grupo: ").append(encabezado.getGrupo()).append("\n"); // <-- Grupo abajo

        // Corte de papel (ESC/POS)
        sb.append("\u001D\u0056\u0001"); // GS + V + 1 → cortar papel parcial

        return sb.toString();
    }

}
