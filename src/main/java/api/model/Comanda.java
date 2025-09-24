package api.model;

import java.util.List;

public class Comanda {
    private String numeroTicket;
    private String mesa;
    private String cliente;
    private String mesero;
    private String estado;
    private List<Producto> productos;

    // Getters y setters
    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }

    public String getMesa() { return mesa; }
    public void setMesa(String mesa) { this.mesa = mesa; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getMesero() { return mesero; }
    public void setMesero(String mesero) { this.mesero = mesero; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    // Método para formatear ticket
    public String formatearTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append("------ TICKET ------\n");
        sb.append("Número: ").append(numeroTicket).append("\n");
        sb.append("Mesa: ").append(mesa).append("\n");
        sb.append("Cliente: ").append(cliente).append("\n");
        sb.append("Mesero: ").append(mesero).append("\n");
        sb.append("-------------------\n");
        for (Producto p : productos) {
            sb.append(p.getProducto()).append(" x").append(p.getCantidad()).append("\n");
        }
        sb.append("-------------------\n");
        sb.append("Estado: ").append(estado).append("\n");
        return sb.toString();
    }
}
