import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.HashMap;

// Classe que realiza a comunicação com RapidAPI
public class RapidAPIInterface {
    // Mapa para guardar os headers que são reutilizados em toda transação
    private HashMap<String,String> rapidApiHeaders;

    // Construtor para inicializar os headers
    public RapidAPIInterface(String xRapidApiHost) {
        this.rapidApiHeaders = new HashMap<>();
        this.rapidApiHeaders.put("x-rapidapi-key", "355db875afmsh4d386a82b3f76f4p1175c3jsnda0b3009667f");
        this.rapidApiHeaders.put("x-rapidapi-host", xRapidApiHost);
    }

    // Método que utiliza o URL raiz do API, o modo e parametros para extrair dados em formato JSON
    public JSONObject extractJsonFromURL(String rawURL, String mode, String[] parameters) throws UnirestException {
        String fullURL = rawURL + "/" + mode;
        if(parameters.length > 0) {
            fullURL += "?";
            for (String param: parameters) {
                fullURL += param + "&";
            }
            fullURL = fullURL.substring(0,fullURL.length()-1);
        }

        HttpResponse<JsonNode> response = Unirest.get(fullURL)
                .headers(this.rapidApiHeaders)
                .asJson();
        return response.getBody().getObject();
    }


}
