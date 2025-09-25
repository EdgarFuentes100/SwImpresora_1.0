package api.model;

public class ApiResponse {
    private boolean ok;
    private String message;
    private Comanda datos;

    // Getters y Setters
    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Comanda getDatos() { return datos; }
    public void setDatos(Comanda datos) { this.datos = datos; }
}
