package ws;

import api.model.ApiResponse;
import api.model.Comanda;
import com.fasterxml.jackson.databind.ObjectMapper;
import print.PrinterService;

import static spark.Spark.*;

public class WstestGui {

    PrinterService printerService = new PrinterService();
    ObjectMapper mapper = new ObjectMapper(); // mapper para JSON

    public void iniciarHttpLocal() {
        port(8081); // puerto local para GUI

        // JSON de prueba
        String jsonPrueba = """
        {
          "ok": true,
          "message": "Éxito",
          "datos": {
            "encabezado": {
              "numeroTicket": 1,
              "cliente": "Edgar",
              "mesero": "Jose",
              "salon": "1",
              "mesa": "MESA 2",
              "fechaHora": "2025-09-24T05:18:41.000Z",
              "grupo": "Bar"
            },
            "productos": [
              {"producto": "Mango", "cantidad": 12},
              {"producto": "FRUI", "cantidad": 12}
            ]
          }
        }
        """;

        // Endpoint para probar impresión
        post("/probar", (req, res) -> {
            String impresora = req.queryParams("impresora"); // nombre de la impresora

            // Convertir JSON a objeto Java
            ApiResponse response = mapper.readValue(jsonPrueba, ApiResponse.class);

            if (response.isOk() && response.getDatos() != null) {
                Comanda comanda = response.getDatos();

                // Formatear para impresión
                String contenidoTermico = comanda.formatearTicketTermico();
                String contenidoNormal = comanda.formatearTicket();

                // Enviar a la impresora
                printerService.imprimirTicket(impresora, contenidoTermico, contenidoNormal);

                return "Ticket enviado a " + impresora;
            } else {
                return "Error: no se pudo obtener la comanda";
            }
        });
    }
}
