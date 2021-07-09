import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.ArrayList;

// Classe para enviar respostas ao front-end
public class COVIDStatsJSONPackager {

    private String frontIP;
    public COVIDStatsJSONPackager(String frontIP) {
        this.frontIP = frontIP;
    }

    // Envia um JSON com a resposta da requisição ao frontIP
    public void sendHTTPResponse(ArrayList<DateDataPair> newCasesList, ArrayList<DateDataPair> recoveredVSDeathsList) {
        JSONObject response = new JSONObject();
        JSONObject datatypes = new JSONObject();
        JSONObject newCases = new JSONObject();
        JSONObject recoveredVSDeaths = new JSONObject();

        for (int i = 0; i < newCasesList.size(); i++) {
            DateDataPair currentPair = newCasesList.get(i);
            newCases.put(currentPair.getDate(), currentPair.getData());
        }

        for (int i = 0; i < recoveredVSDeathsList.size(); i++) {
            DateDataPair currentPair = recoveredVSDeathsList.get(i);
            recoveredVSDeaths.put(currentPair.getDate(), currentPair.getData());
        }

        datatypes.put("new-cases", newCases);
        datatypes.put("recovered-vs-deaths", recoveredVSDeaths);
        response.put("response", datatypes);

        Unirest.put(this.frontIP).body(response);
    }

    // "Execução" falsa das funções do back-end
    public static void main(String[] args) throws UnirestException {
        COVIDStatsProvider covidStatsProvider = new COVIDStatsProvider();
        ArrayList<DateDataPair> newCasesList = covidStatsProvider.newCasesLastNDays(7,"brazil");
        ArrayList<DateDataPair> recoveredVSDeathsList = covidStatsProvider.recoveredCasesMinusDeathsLastNDays(7, "brazil");
        COVIDStatsJSONPackager packager = new COVIDStatsJSONPackager("localhost");
        packager.sendHTTPResponse(newCasesList, recoveredVSDeathsList);
    }

}
