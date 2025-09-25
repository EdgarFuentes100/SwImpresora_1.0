import api.ApiClient;
import model.grupoPrinterModel;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestApiClient {
    public static void main(String[] args) {
        try {
            ApiClient api = new ApiClient();
            ObjectMapper mapper = new ObjectMapper();

            // 1. GET /gruposprinter
            String getResponse = api.get("/grupoPrinter/ListaGrupoPrinter");
            System.out.println(getResponse);

            /*
            // 2. POST /gruposprinter
            grupoPrinterModel nuevoGrupo = new grupoPrinterModel(0, "Soporte Técnico", "HP-LaserJet-P1102");
            String postResponse = api.post("/gruposprinter", nuevoGrupo);
            System.out.println("POST /gruposprinter:");
            System.out.println(postResponse);

            // 3. PUT /gruposprinter/1
            grupoPrinterModel grupoActualizado = new grupoPrinterModel(1, "Soporte Técnico", "Epson-XP241");
            String putResponse = api.put("/gruposprinter/1", grupoActualizado);
            System.out.println("PUT /gruposprinter/1:");
            System.out.println(putResponse);

            // 4. DELETE /gruposprinter/1
            String deleteResponse = api.delete("/gruposprinter/1");
            System.out.println("DELETE /gruposprinter/1:");
            System.out.println(deleteResponse);
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
