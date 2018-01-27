import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static java.lang.Double.parseDouble;

public class stock {

    private static JFrame frame = new JFrame("Cryptocurrency App");
    private static JTabbedPane tabbedPane = new JTabbedPane();
    private static ArrayList<String> addedPanes = new ArrayList<>();

    public static void main(String[] args){
        System.out.println("Starting...");
        buildAllPanels();

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);

                frame.setPreferredSize(new Dimension(600, 320));
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

                //Add content to the window.
                JMenuBar menuBar;
                JMenu menu;
                JMenuItem menuItem;


                //Create the menu bar.
                menuBar = new JMenuBar();

                //Build the first menu.
                menu = new JMenu("Options");
                menu.setMnemonic(KeyEvent.VK_A);
                menu.getAccessibleContext().setAccessibleDescription(
                        "The only menu in this program that has menu items");
                menuBar.add(menu);

                //a group of JMenuItems
                menuItem = new JMenuItem("Refresh Data",
                        KeyEvent.VK_T);
                menuItem.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_1, ActionEvent.ALT_MASK));
                menuItem.getAccessibleContext().setAccessibleDescription(
                        "Refresh Cryptocurrency Data");
                menuItem.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        tabbedPane.removeAll();
                        buildAllPanels();
                    }
                });
                menu.add(menuItem);

                menuItem = new JMenuItem("Set Alert",
                        KeyEvent.VK_T);
                menuItem.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_1, ActionEvent.ALT_MASK));
                menuItem.getAccessibleContext().setAccessibleDescription(
                        "This doesn't really do anything");
                menuItem.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        buildAlertPanel();
                        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Set Alert"));
                    }
                });
                menu.add(menuItem);

                menuItem = new JMenuItem("Add Cryptocurrency",
                        new ImageIcon("images/middle.gif"));
                menuItem.setMnemonic(KeyEvent.VK_B);
                menu.add(menuItem);
                menuItem.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        buildAddPanel();
                        int index = tabbedPane.indexOfTab("Add");
                        tabbedPane.setSelectedIndex(index);
                    }
                });


                menuBar.add(menu);


                frame.setJMenuBar(menuBar);

                frame.getContentPane().add(tabbedPane);

                //Display the window.
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private static JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    private static JComponent makeAddPanel() {
        JPanel panel = new JPanel(false);
        JLabel heading = new JLabel("Enter correctly-spelled name of cryptocurrency: ");
        JTextField text = new JTextField(1);
        JButton adder = new JButton("Add Cryptocurrency");
        adder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userIn = text.getText();
                if(((getData((userIn.toLowerCase()).replace(" ", "-"))))[1] != null){
                    addedPanes.add((userIn/*.toLowerCase()).replace(" ", "-"*/));
                    buildPanel(userIn, 30);
                    //makeAddPanel();
                    tabbedPane.removeTabAt(tabbedPane.indexOfTab("Add"));
                }else{
                    System.err.println("Unknown cryptocurrency.");
                }
            }
        });
        panel.setLayout(new GridLayout(3, 1));
        panel.add(heading);
        panel.add(text);
        panel.add(adder);
        return panel;
    }
    private static String limit = "less";
    private static JComponent makeAlertPanel() {
        JPanel panel = new JPanel(false);
        JLabel heading = new JLabel("Enter correctly-spelled name of cryptocurrency: ");
        JTextField text = new JTextField(1);
        JRadioButton more = new JRadioButton("Alert when price exceeds given amount");
        more.addActionListener(new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent e){
               limit = "more";
           }
        });
        JRadioButton less = new JRadioButton("Alert when price goes below given amount");
        less.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                limit = "less";
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(more);
        group.add(less);
        JLabel head = new JLabel("Enter maximum/minimum price: ");
        JTextField price = new JTextField(1);
        JButton adder = new JButton("Set Alert");
        adder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userCrypto = text.getText();
                double userPrice = parseDouble(((price.getText()).replace("$", "").replace(",","")));
                if((((getData((userCrypto.toLowerCase()).replace(" ", "-"))))[1] != null) && userPrice >= 0 && limit != null){
                    tabbedPane.removeTabAt(tabbedPane.indexOfTab("Set Alert"));
                    /*
                    java.util.Timer timer = new java.util.Timer(true);
                    timer.schedule(setAlert(userCrypto, userPrice), 0, 300000);
                    */
                    setAlert(userCrypto, userPrice);
                }else{
                    System.err.println("Unknown cryptocurrency.");
                }
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
    private static void setAlert(String n, double p){
        Thread t = new Thread(new Runnable(){
            public void run() {
                boolean run = true;
                try {
                    while (run) {
                        double price = parseDouble(((getData((n.toLowerCase()).replace(" ", "-"))))[1]);
                        //System.out.println("Checked for " + n);
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
            }});
        t.start();
    }
    /** Returns an ImageIcon, or null if the path was invalid. */
    private static ImageIcon createImageIcon(String path) {
        try {
            java.net.URL imgURL = new URL("https://walletinvestor.com/static/frontend/images/cryptocurrency-news-icon.png");
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
    private static ImageIcon createCoinIcon(String name) {
        try {
            java.net.URL imgURL = new URL("https://files.coinmarketcap.com/static/img/coins/32x32/" + name + ".png");
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Couldn't find file: " + name);
                return null;
            }
        }catch(MalformedURLException e){
            System.err.println("Malformed URL Exception");
        }
        return null;
    }
    private static void buildPanel(String c, int series){
        String mod = c.toLowerCase();
        String modded = mod.replace(" ", "-");
        ImageIcon icon = createCoinIcon(modded);
        String[] data = getData(modded);
        if(data[1]!=null) {
            JComponent panel1 = makeTextPanel("<html>" + c + "<br/>" + "Price: $" + data[1] + "<br/>" + "Market Cap: $" + data[2] + "<br/>" + "Supply: " + data[3] + " " + c + "<br/> One-hour Price Change: " + data[4] + "% <br/> 24-Hour Change: " + data[5] + "% </html>");
            tabbedPane.addTab(c, icon, panel1, "Cryptocurrency");
        }
    }

    private static void buildAddPanel(){
        ImageIcon icon = createImageIcon("images/middle.gif");
        JComponent panel1 = makeAddPanel();
        tabbedPane.addTab("Add", icon, panel1, "Adds a tab for the cryptocurrency of your choice");
    }
    private static void buildAlertPanel(){
        ImageIcon icon = createImageIcon("images/middle.gif");
        JComponent panel1 = makeAlertPanel();
        tabbedPane.addTab("Set Alert", icon, panel1, "Sets a price alert for a particular cryptocurrency");
    }
    private static void buildHotPanel(){
        ImageIcon icon = createImageIcon("images/middle.gif");
        String[] crypto = {"bitcoin", "ethereum", "ripple", "bitcoin-cash", "cardano", "litecoin", "nem", "stellar", "iota", "eos", "neo", "dash", "tron", "monero", "bitcoin-gold", "ethereum-classic", "qtum", "icon", "lisk", "raiblocks"};
        double firstHighest = -100;
        String firstHighestName = "";
        double secondHighest = -100;
        String secondHighestName = "";
        double thirdHighest = -100;
        String thirdHighestName = "";
        double fourthHighest = -100;
        String fourthHighestName = "";
        double fifthHighest = -100;
        String fifthHighestName = "";
        for(int i = 0; i < crypto.length; i++) {
            String[] c = getData(crypto[i]);
            String me = c[5];
            double m = parseDouble(me);
            if(m>=firstHighest){
                fifthHighest = fourthHighest;
                fifthHighestName = fourthHighestName;
                fourthHighest = thirdHighest;
                fourthHighestName = thirdHighestName;
                thirdHighest = secondHighest;
                thirdHighestName = secondHighestName;
                secondHighest = firstHighest;
                secondHighestName = firstHighestName;
                firstHighest = m;
                firstHighestName = c[0];
            }else if(m>=secondHighest){
                fifthHighest = fourthHighest;
                fifthHighestName = fourthHighestName;
                fourthHighest = thirdHighest;
                fourthHighestName = thirdHighestName;
                thirdHighest = secondHighest;
                thirdHighestName = secondHighestName;
                secondHighest = m;
                secondHighestName = c[0];
            }else if(m>=thirdHighest){
                fifthHighest = fourthHighest;
                fifthHighestName = fourthHighestName;
                fourthHighest = thirdHighest;
                fourthHighestName = thirdHighestName;
                thirdHighest = m;
                thirdHighestName = c[0];
            }else if(m>=fourthHighest){
                fifthHighest = fourthHighest;
                fifthHighestName = fourthHighestName;
                fourthHighest = m;
                fourthHighestName = c[0];
            }else if(m>=fifthHighest){
                fifthHighest = m;
                fifthHighestName = c[0];
            }
        }
        JComponent panel1 = makeTextPanel("<html>Hottest Cryptocurrencies: <br/>" + firstHighestName + " : " + firstHighest + "% <br/>"+ secondHighestName + " : " + secondHighest + "% <br/>"+ thirdHighestName + " : " + thirdHighest + "% <br/>"+ fourthHighestName + " : " + fourthHighest + "% <br/>"+ fifthHighestName + " : " + fifthHighest + "% <br/>");
        tabbedPane.addTab("Hottest Cryptocurrencies", icon, panel1, "Cryptocurrencies with the greatest 24-hour price increase");
    }
    private static void buildAllPanels(){
        buildHotPanel();
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
        /*
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
        */
        if(addedPanes.size() > 0) {
            for (int i = 0; i < addedPanes.size(); i++) {
                buildPanel(addedPanes.get(i), 30);
            }
        }
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
