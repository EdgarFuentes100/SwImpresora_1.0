package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GrupoPrinterResponse {

    @JsonProperty("ok")
    public boolean success;

    public String message;

    @JsonProperty("datos")
    public List<grupoPrinterModel> data;
}
