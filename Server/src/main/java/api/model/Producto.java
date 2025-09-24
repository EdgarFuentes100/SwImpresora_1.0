package api.model;

public class Producto {
    private int idImpresion;
    private int numeroTicket;
    private String cliente;
    private String mesero;
    private String salon;
    private String mesa;
    private String fechaHora;
    private String producto;
    private int cantidad;
    private String grupo;
    private String estado;

    // Getters y setters

    public int getIdImpresion() { return idImpresion; }
    public void setIdImpresion(int idImpresion) { this.idImpresion = idImpresion; }

    public int getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(int numeroTicket) { this.numeroTicket = numeroTicket; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getMesero() { return mesero; }
    public void setMesero(String mesero) { this.mesero = mesero; }

    public String getSalon() { return salon; }
    public void setSalon(String salon) { this.salon = salon; }

    public String getMesa() { return mesa; }
    public void setMesa(String mesa) { this.mesa = mesa; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
