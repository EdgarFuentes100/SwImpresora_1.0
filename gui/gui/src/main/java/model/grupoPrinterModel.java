package model;

public class grupoPrinterModel {
    private int idGrupoPrinter;
    private String nombre;
    private String impresora;

    public grupoPrinterModel() {
        // Constructor vacío necesario para Jackson
    }

    public grupoPrinterModel(int idGrupoPrinter, String nombre, String impresora) {
        this.idGrupoPrinter = idGrupoPrinter;
        this.nombre = nombre;
        this.impresora = impresora;
    }

    // Getters y Setters
    public int getIdGrupoPrinter() {
        return idGrupoPrinter;
    }

    public void setIdGrupoPrinter(int idGrupoPrinter) {
        this.idGrupoPrinter = idGrupoPrinter;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImpresora() {
        return impresora;
    }

    public void setImpresora(String impresora) {
        this.impresora = impresora;
    }

    @Override
    public String toString() {
        return "GrupoPrinter{" +
                "idGrupoPrinter=" + idGrupoPrinter +
                ", nombre='" + nombre + '\'' +
                ", impresora='" + impresora + '\'' +
                '}';
    }
}
