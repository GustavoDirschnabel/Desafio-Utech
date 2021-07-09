import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

// Classe para obter estatísticas de COVID
public class COVIDStatsProvider {

    private RapidAPIInterface rapidAPI;
    // X-RAPIDAPI-HOST da API de COVID
    private String rawQueryURL = "https://covid-193.p.rapidapi.com/";
    // HashMap para armazenar os dados já obtidos de uma data
    private HashMap<String, JSONObject> recoveredData;

    // Inicializa a interface e o HashMap
    public COVIDStatsProvider() {
        rapidAPI = new RapidAPIInterface("covid-193.p.rapidapi.com");
        recoveredData = new HashMap<>();
    }

    // Retorna o JSON de uma requisição do modo "history" no país e data informados.
    // Armazena os objetos retornados da interface com RapidApi usando data como chave, criando uma espécie de "cache"
    // para as consultas à API.
    private JSONObject getHistoryFromJSON(String country, String dateYYYYMMDD) throws UnirestException {
        JSONObject dataObj;
        if (this.recoveredData.containsKey(dateYYYYMMDD)){
            dataObj = this.recoveredData.get(dateYYYYMMDD);
        } else {
            JSONObject obj = this.rapidAPI.extractJsonFromURL(this.rawQueryURL, "history",
                    new String[]{"country=" + country, "day=" + dateYYYYMMDD});
            dataObj = obj.getJSONArray("response").getJSONObject(0);
            this.recoveredData.put(dateYYYYMMDD, dataObj);
        }
        return dataObj;
    }

    // Retorna os novos casos registrados em um dia.
    public int newCasesOnDate(String country, String dateYYYYMMDD) {
        try {
            int cases = this.getHistoryFromJSON(country,dateYYYYMMDD).getJSONObject("cases").getInt("new");
            return cases;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Retorna o número de mortes registrados em um dia.
    public int newDeathsOnDate(String country, String dateYYYYMMDD) {
        try {
            int deaths = this.getHistoryFromJSON(country,dateYYYYMMDD).getJSONObject("deaths").getInt("new");
            return deaths;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Retorna o número novo de casos recuperados em um dia a partir da subtração do total de recuperados desse dia
    // com o do dia anterior.
    public int newRecoveredCasesOnDate(String country, LocalDateTime targetDate) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = dtf.format(targetDate);
        try {
            int recoveredOnDate = this.getHistoryFromJSON(country,formattedDate).getJSONObject("cases").getInt("recovered");

            formattedDate = dtf.format(targetDate.minusDays(1));
            int recoveredOnPreviousDate = this.getHistoryFromJSON(country,formattedDate).getJSONObject("cases").getInt("recovered");
            return recoveredOnDate - recoveredOnPreviousDate;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Retorna, para "n" dias, o número de recuperados-mortes de cada um.
    public ArrayList<DateDataPair> recoveredCasesMinusDeathsLastNDays(int n, String country) {
        ArrayList<DateDataPair> diffs = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime currentDate = LocalDateTime.now();
        String formattedDate;

        for (int i = 0; i < n; i++) {
            formattedDate = dtf.format(currentDate.minusDays(i));
            int dif = this.newRecoveredCasesOnDate(country, currentDate.minusDays(i)) - this.newDeathsOnDate(country, formattedDate);
            diffs.add(new DateDataPair(formattedDate, dif));
        }

        return diffs;
    }


    // Retorna, para "n" dias, o número de novos casos.
    public ArrayList<DateDataPair> newCasesLastNDays(int n, String country) {
        ArrayList<DateDataPair> newCases = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime currentDate = LocalDateTime.now();
        String formattedDate;

        for (int i = 0; i < n; i++) {
            formattedDate = dtf.format(currentDate.minusDays(i));
            newCases.add(new DateDataPair(formattedDate, this.newCasesOnDate(country, formattedDate)));
        }

        return newCases;
    }
}
