import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.GridLayout;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class stock {
    //Main display class
    protected static JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        try {
            java.net.URL imgURL = new URL("http://files.coinmarketcap.com.s3-website-us-east-1.amazonaws.com/static/img/coins/200x200/bitcoin.png");
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        }catch(MalformedURLException e){
            System.err.println("Malformed URL Exception");
        }
        return null;
    }

    public static JTabbedPane tabbedPane = new JTabbedPane();

    public static void buildPanel(String c, int series){
        ImageIcon icon = createImageIcon("images/middle.gif");
        String mod = c.toLowerCase();
        String modded = mod.replace(" ", "-");
        String[] data = getData(modded);
        JComponent panel1 = makeTextPanel("<html>"+c+"<br/>"+ "Price: $" + data[1] + "<br/>" + "Market Cap: $" + data[2] + "<br/>" + "Supply: " + data[3] + " " + c + "<br/> One-hour Price Change: " + data[4] + "% <br/> 24-Hour Change: " + data[5] + "% </html>");
        tabbedPane.addTab(c, icon, panel1, "Cryptocurrency");
    }

    public static void main(String[] args){

        buildPanel("Bitcoin", 1);
        buildPanel("Ethereum", 2);
        buildPanel("Ripple", 3);
        buildPanel("Bitcoin Cash", 4);
        buildPanel("Cardano", 5);
        buildPanel("Litecoin", 6);
        buildPanel("NEM", 7);
        buildPanel("Stellar", 8);
        buildPanel("IOTA", 9);
        buildPanel("EOS", 10);
        buildPanel("NEO", 11);
        buildPanel("Dash", 12);
        buildPanel("TRON", 13);
        buildPanel("Monero", 14);
        buildPanel("Bitcoin Gold", 15);
        buildPanel("Ethereum Classic", 16);
        buildPanel("Qtum", 17);
        buildPanel("ICON", 18);
        buildPanel("Lisk", 19);
        buildPanel("Raiblocks", 20);


        //Add the tabbed pane to this panel.
        //add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                JFrame frame = new JFrame("Cryptocurrency App");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //Add content to the window.

                frame.getContentPane().add(tabbedPane);

                //Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private static JSONObject builder(String symbol) {

        try {
            InputStream is = new URL("https://api.coinmarketcap.com/v1/ticker/"+symbol+"/").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            //System.out.println(jsonText);
            File temp = File.createTempFile("crypto", ".json");

            //write it
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            bw.write(jsonText);
            bw.close();

            JSONParser parser = new JSONParser();
            try
            {
                Object object = parser.parse(new FileReader(temp));

                //convert Object to JSONObject
                JSONArray jsonObject = (JSONArray)object;
                JSONObject json = (JSONObject)jsonObject.get(0);
                return json;
            }
            catch(FileNotFoundException fe)
            {
                fe.printStackTrace();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }catch(MalformedURLException e){
            System.err.println("MalformedURLException, lines 49-54.");
        }catch(IOException e){
            System.err.println("IOException, lines 49-54.");
        }catch(Exception e){
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
        //System.out.println(sb.toString());
        return sb.toString();
    }
    private static String[] getData(String symbol){
        JSONObject json = builder(symbol);
        try {
            json.toString();
            //System.out.println(json);

            String name = json.get("name").toString();
            String price = json.get("price_usd").toString();
            String mkcap = json.get("market_cap_usd").toString();
            String supply = json.get("total_supply").toString();
            String h1change = json.get("percent_change_1h").toString();
            String h24change = json.get("percent_change_24h").toString();

            String[] dataFinal = {name, price, mkcap, supply, h1change, h24change};
            return dataFinal;
        }catch(Exception e){
            System.err.println("JSONException, lines 76-83. " + symbol);
        }
        return null;
    }
}