import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class stock {
    //Main display class
    public static void main(String[] args){
        //1. Create the frame.
        JFrame frame = new JFrame("Stock Picker");

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //3. Create components and put them in the frame.
        //...create emptyLabel...
        Label emptyLabel = new Label();

        String[] columnNames = {"Symbol", "Open", "High"
                , "Low", "Close", "Volume"};
        String[] stock = {"ATVI", "AAPL", "AMZN", "GOOG", "FB", "NFLX", "AMD", "ADBE", "INTC", "NVDA"};
        String[] crypto = {"BTC", "XRP", "ETH", "BCH", "ADA", "TRX", "XEM", "LTC", "XLM", "MIOTA"};
        Object[][] data = getObject(stock);
        Object[][] cdata = getObject(crypto);
        JTable table = new JTable(data, columnNames);
        JTable ctable = new JTable(cdata, columnNames);
        /*
        JScrollPane tableScrollPane = new JScrollPane(table);
        JScrollPane ctableScrollPane = new JScrollPane(ctable);
        */
        frame.getContentPane().add(table, BorderLayout.CENTER);
        frame.getContentPane().add(ctable, BorderLayout.SOUTH);
        //4. Size the frame.
        frame.pack();

        //5. Show it.
        frame.setVisible(true);
    }
    //Data compiler, I think    Alpha Vantage API Key: BZ0EQQXCMAZ9J7KL
    private static JSONObject builder(String symbol) {
        try {
            InputStream is = new URL("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=1min&apikey=BZ0EQQXCMAZ9J7KL").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            is.close();
            return json;
        }catch(MalformedURLException e){
            System.err.println("MalformedURLException, lines 49-54.");
        }catch(IOException e){
            System.err.println("IOException, lines 49-54.");
        }catch(JSONException e){
            System.err.println("JSONException, lines 49-54.");
        }
        return null;
    }
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    private static String[] getData(String symbol){
        JSONObject json = builder(symbol);

        try {
            //json.toString();
            String open = json.getJSONObject("open")/*.getJSONObject("Time Series (1min)")*/.toString();
            String high = json.getJSONObject("high")/*.getJSONObject("Time Series (1min)")*/.toString();
            String low = json.getJSONObject("low")/*.getJSONObject("Time Series (1min)")*/.toString();
            String close = json.getJSONObject("close")/*.getJSONObject("Time Series (1min)")*/.toString();
            String volume = json.getJSONObject("volume")/*.getJSONObject("Time Series (1min)")*/.toString();
            String[] data = {symbol, open, high, low, close, volume};
            return data;
        }catch(JSONException e){
            System.err.println("JSONException, lines 76-83. " + symbol);
        }
        return null;
    }
    private static Object[][] getObject(String[] stocks){
        Object[][] table = {
                {1, 1, 1, 1, true, "hey"},
                {1, 1, 1, 1, true, "hey"},
                {1, 1, 1, 1, true, "hey"},
                {1, 1, 1, 1, true, "hey"},
                {1, 1, 1, 1, true, "hey"},//5
                {1, 1, 1, 1, true, "hey"},
                {1, 1, 1, 1, true, "hey"},
                {1, 1, 1, 1, true, "hey"},
                {1, 1, 1, 1, true, "hey"},
                {1, 1, 1, 1, true, "hey"}//10
        };
        for(int i = 0; i < stocks.length; i++){
            table[i]=getData(stocks[i]);
        }
        return table;
    }
}
