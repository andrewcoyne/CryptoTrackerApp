import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;
import org.json.simple.JSONObject; // com.github.cliftonlabs:json-simple:2.3.1 via Maven
import org.json.simple.parser.JSONParser;
import static java.lang.Double.parseDouble;

public class cryptoapp {

    private static final JFrame frame = new JFrame("Cryptocurrency App");
    private static final JTabbedPane tabbedPane = new JTabbedPane();
    private static final ArrayList<String> addedPanes = new ArrayList<>();

    public static void main(String[] args){
        System.out.println("Starting...");

        new Thread(() -> {
            frame.setTitle("Cryptocurrency App (Loading Data...)");
            buildAllPanels();
            frame.setTitle("Cryptocurrency App");
        }).start();

        // the below code sets up the application window
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        SwingUtilities.invokeLater(() -> {
            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);

            frame.setPreferredSize(new Dimension(450, 230));
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            //Add content to the window.
            JMenuBar menuBar;
            JMenu menu;
            JMenuItem menuItem;


            //Create the menu bar.
            menuBar = new JMenuBar();

            //Build the first menu.
            menu = new JMenu("Options");
            menuBar.add(menu);

            //a group of JMenuItems
            menuItem = new JMenuItem("Refresh Data");
            menuItem.getAccessibleContext().setAccessibleDescription(
                    "Refresh Cryptocurrency Data");
            menuItem.addActionListener(e -> {
                tabbedPane.removeAll();
                new Thread(() -> {
                    frame.setTitle("Cryptocurrency App (Loading Data...)");
                    buildAllPanels();
                    frame.setTitle("Cryptocurrency App");
                }).start();
            });
            menu.add(menuItem);

            menuItem = new JMenuItem("Set Alert");
            menuItem.addActionListener(e -> {
                buildAlertPanel();
                tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Set Alert"));
            });
            menu.add(menuItem);

            menuItem = new JMenuItem("Add Cryptocurrency");
            menu.add(menuItem);
            menuItem.addActionListener(e -> {
                buildAddPanel();
                int index = tabbedPane.indexOfTab("Add");
                tabbedPane.setSelectedIndex(index);
            });

            menuBar.add(menu);

            frame.setJMenuBar(menuBar);

            frame.getContentPane().add(tabbedPane);

            //Display the window.
            frame.pack();
            frame.setVisible(true);
        });
    }

    // creates a text box for displaying the information
    private static JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    // creates the tab that lets a user add a cryptocurrency tab
    private static JComponent makeAddPanel() {
        JPanel panel = new JPanel(false);
        JLabel heading = new JLabel("Enter correctly-spelled name of cryptocurrency: ");
        JTextField text = new JTextField(1);
        JButton adder = new JButton("Add Cryptocurrency");
        adder.addActionListener(e -> {
            String userIn = text.getText();

            try {
                String userInMod = getData((userIn.toLowerCase()).replace(" ", "-"))[1];
                if (userInMod != null) {
                    addedPanes.add((userInMod));
                    buildPanel(userIn);
                    tabbedPane.removeTabAt(tabbedPane.indexOfTab("Add"));
                } else {
                    System.err.println("Unknown cryptocurrency.");
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        panel.setLayout(new GridLayout(3, 1));
        panel.add(heading);
        panel.add(text);
        panel.add(adder);
        return panel;
    }
    private static String limit = "less";

    // creates the tab that lets a user set a crypto price alert
    private static JComponent makeAlertPanel() {
        JPanel panel = new JPanel(false);
        JLabel heading = new JLabel("Enter correctly-spelled name of cryptocurrency: ");
        JTextField text = new JTextField(1);
        JRadioButton more = new JRadioButton("Alert when price exceeds given amount");
        more.addActionListener(e -> limit = "more");
        JRadioButton less = new JRadioButton("Alert when price goes below given amount");
        less.addActionListener(e -> limit = "less");
        ButtonGroup group = new ButtonGroup();
        group.add(more);
        group.add(less);
        JLabel head = new JLabel("Enter maximum/minimum price: ");
        JTextField price = new JTextField(1);
        JButton adder = new JButton("Set Alert");
        adder.addActionListener(e -> {
            String userCrypto = text.getText();
            double userPrice = parseDouble(((price.getText()).replace("$", "").replace(",","")));
            String userCryptoMod = ((Objects.requireNonNull(getData((userCrypto.toLowerCase()).replace(" ", "-")))))[1];
            if((userCryptoMod != null) && userPrice >= 0 && limit != null){
                tabbedPane.removeTabAt(tabbedPane.indexOfTab("Set Alert"));
                setAlert(userCrypto, userPrice);
            }else{
                System.err.println("Unknown cryptocurrency.");
            }
        });
        panel.setLayout(new GridLayout(4, 1));
        panel.add(heading);
        panel.add(text);
        panel.add(more);
        panel.add(less);
        panel.add(head);
        panel.add(price);
        panel.add(adder);
        return panel;
    }

    // continually checks the crypto price to see if it meets the user-provided criteria for an alert
    private static void setAlert(String n, double p){
        Thread t = new Thread(() -> {
            boolean run = true;
            try {
                while (run) {
                    double price = parseDouble(((Objects.requireNonNull(getData(n)))[1]));
                    if (limit.equals("more")) {
                        if (price > p) {
                            JFrame temp = new JFrame("Alert");
                            JLabel j = new JLabel(("" + n + " went above $" + p));
                            JPanel jk = new JPanel();
                            jk.add(j);
                            temp.add(jk);
                            temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            temp.setPreferredSize(new Dimension(250, 100));
                            temp.pack();
                            temp.setVisible(true);
                            run = false;
                        }
                    } else {
                        if (price < p) {
                            JFrame temp = new JFrame("Alert");
                            JLabel j = new JLabel(("" + n + " went below $" + p));
                            JPanel jk = new JPanel();
                            jk.add(j);
                            temp.add(jk);
                            temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            temp.setPreferredSize(new Dimension(250, 100));
                            temp.pack();
                            temp.setVisible(true);
                            run = false;
                        }
                    }
                    //return null;
                    Thread.sleep(300000);
                }
            } catch (InterruptedException e) {
                System.err.println("InterruptedException.");
            }
        });
        t.start();
    }

    // gets the flame icon next to "Hottest Cryptocurrencies" the Hot tab
    private static ImageIcon createImageIcon() {
        try {
            return new ImageIcon(new URL("https://walletinvestor.com/static/frontend/images/cryptocurrency-news-icon.png"));
        }catch(MalformedURLException e){
            System.err.println("Malformed URL Exception");
        }
        return null;
    }

    // gets the little icons next to the crypto names on each tab. currently does not work
    private static ImageIcon createCoinIcon(String name) {
        try {
            return new ImageIcon(new URL("https://files.coinmarketcap.com/static/img/coins/32x32/" + name + ".png"));
        }catch(MalformedURLException e){
            System.err.println("Malformed URL Exception");
        }
        return null;
    }

    // constructs each tab
    private static void buildPanel(String c){
        String mod = c.toLowerCase();
        String modded = mod.replace(" ", "-");
        ImageIcon icon = createCoinIcon(modded);
        try {
            String[] data = getData(modded);
            assert data != null;
            if (data[1] != null) {
                JComponent panel1 = makeTextPanel("<html>" + c + "<br/>" + "Price: $" + data[1] + "<br/>" + "Market Cap: $" + data[2] + "<br/>" + "24-Hour Volume: " + data[3] + " " + c + "<br/> 24-hour Price Change: " + data[4] + "% </html>");
                tabbedPane.addTab(c, icon, panel1, "Cryptocurrency");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    // makes the tab that lets a user add a new cryptocurrency tab
    private static void buildAddPanel(){
        ImageIcon icon = createImageIcon();
        JComponent panel1 = makeAddPanel();
        tabbedPane.addTab("Add", icon, panel1, "Adds a tab for the cryptocurrency of your choice");
    }

    // makes the tab that lets a user request a price alert for a cryptocurrency
    private static void buildAlertPanel(){
        ImageIcon icon = createImageIcon();
        JComponent panel1 = makeAlertPanel();
        tabbedPane.addTab("Set Alert", icon, panel1, "Sets a price alert for a particular cryptocurrency");
    }

    // makes the tab displaying the hottest cryptocurrencies
    private static void buildHotPanel(){
        ImageIcon icon = createImageIcon();
        String[] crypto = {"Bitcoin", "Ethereum", "Tether", "Zcash", "Litecoin", "Bitcoin Cash", "Dogecoin", "Polkadot", "Cardano", "Chainlink", "Stellar", "Dash", "Monero", "Wrapped Bitcoin", "EOS", "NEM", "Tron", "OKB", "NEO", "Tezos", "Celsius Network","RenBTC","Huobi BTC","Aave ETH"};
        ArrayList<Double> highPrice = new ArrayList<>();
        highPrice.add(-100.0);
        ArrayList<String> highName = new ArrayList<>();
        highName.add("None");
        for (String s : crypto) {
            String[] cryptoData = getData(s);
            if(cryptoData != null) {
                String priceStr = cryptoData[4];
                double increase;
                if(priceStr.equals("Error: Not Found") || priceStr.equals("null")) {
                    increase = 0;
                }else{
                    increase = parseDouble(priceStr);
                }
                ArrayList<Double> highPrice2 = new ArrayList<>(highPrice);
                for (double incr : highPrice2) {
                    if (increase > incr) {
                        int index = highPrice.indexOf(incr);
                        highName.add(index, s);
                        highPrice.add(index, increase);
                        break;
                    }
                }
            }
        }
        JComponent panel1 = makeTextPanel("<html>Hottest Cryptocurrencies: <br/>" + highName.get(0) + " : " + highPrice.get(0) + "% <br/>"+ highName.get(1) + " : " + highPrice.get(1) + "% <br/>"+ highName.get(2) + " : " + highPrice.get(2) + "% <br/>"+ highName.get(3) + " : " + highPrice.get(3) + "% <br/>"+ highName.get(4) + " : " + highPrice.get(4) + "% <br/>");
        tabbedPane.addTab("Hottest Cryptocurrencies", icon, panel1, "Cryptocurrencies with the greatest 24-hour price increase");
    }

    // builds the tab for each default cryptocurrency
    private static void buildAllPanels(){
        buildHotPanel();
        buildPanel("Bitcoin");
        buildPanel("Ethereum");
        buildPanel("Tether");
        buildPanel("Zcash");
        buildPanel("Litecoin");
        buildPanel("Bitcoin Cash");
        buildPanel("Dogecoin");
        buildPanel("Polkadot");
        buildPanel("Cardano");
        buildPanel("Chainlink");
        if(addedPanes.size() > 0) {
            for (String addedPane : addedPanes) {
                buildPanel(addedPane);
            }
        }
    }

    // makes the crypto data request and packages it into a JSONObject
    private static JSONObject builder(String symbol) {
        // powered by CoinGecko API
        try {
            InputStream is = new URL("https://api.coingecko.com/api/v3/simple/price?ids=" + symbol.toLowerCase().replace(" ","-") + "&vs_currencies=USD&include_market_cap=true&include_24hr_vol=true&include_24hr_change=true").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            File temp = File.createTempFile("cryptoapp", ".json");

            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            bw.write(jsonText);
            bw.close();

            try{
                JSONParser parser = new JSONParser();
                JSONObject obj  = (JSONObject) parser.parse(new FileReader(temp));

                return obj;
            } catch(Exception e) {
                e.printStackTrace();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // reads through the JSON output to turn it into a String
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    // converts the JSONObject from builder() to a String and gets the crypto data from it
    private static String[] getData(String symbol){
        JSONObject json = builder(symbol);
        if(json.toString().equals("{}")){
            if(!symbol.endsWith("-token")) {
                return getData(symbol + "-token");
            }

            return new String[]{"Error: Not Found","Error: Not Found",
                    "Error: Not Found","Error: Not Found","Error: Not Found"};
        }
        if(json.size() < 3){
            json = builder(symbol.replace("-"," "));
        }

        try {
            String price = finder(json,"usd");
            String mkcap = finder(json,"usd_market_cap");
            String dayvol = finder(json,"usd_24h_vol");
            String daychange = finder(json, "usd_24h_change");

            return new String[]{symbol, price, mkcap, dayvol, daychange};
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // finds the data requested within the text of a json request
    private static String finder(JSONObject json, String data){
        String j = json.toString();
        String query = "\"" + data + "\":";
        int dataStart = j.indexOf(query) + query.length();
        int dataEnd = j.indexOf(",", dataStart);
        if(dataEnd == -1){
            dataEnd = j.indexOf("}",dataStart);
        }
        return j.substring(dataStart, dataEnd);
    }
}
