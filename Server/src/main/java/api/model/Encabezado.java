package api.model;

public class Encabezado {
    private String numeroTicket;
    private String cliente;
    private String mesero;
    private String salon;
    private String mesa;
    private String fechaHora;
    private String grupo;

    // Getters y Setters
    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }

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

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
}
