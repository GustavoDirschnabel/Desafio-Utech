// Classe para armazenar duplas de dados do tipo <String,Integer> de uma forma mais organizada que um array e com
// menos overhead que um HashMap.
public class DateDataPair {
    private String date;
    private int data;

    public DateDataPair(){
        data = 0;
        date = "";
    }

    public DateDataPair(String date, int data) {
        this.data = data;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "[" + this.date + ", " + this.data + "]";
    }
}
