package api.model;

import java.util.List;

public class ApiResponse {
    private boolean ok;
    private String message;
    private List<Producto> datos;

    // getters y setters
    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Producto> getDatos() { return datos; }
    public void setDatos(List<Producto> datos) { this.datos = datos; }
}
