package resource.data;

import model.MarketInfo;
import model.order.Order;
import model.order.OrderType;

import java.io.*;
import java.util.*;

public final class FileShareCreator extends ShareCreator {

    private final String file;
    private final Set<String> allStocks = new LinkedHashSet<>();
    private final MarketInfo initialPrices = new MarketInfo();
    private final List<Order> initialSellOrders = new LinkedList<>();

    public FileShareCreator(String file) throws IOException {
        this.file = file;
    }

    @Override
    public void initializeShares() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        while((line = reader.readLine()) != null) {
            String[] info = line.split(",");
            if(info.length < 3) throw new IOException("Malformed input");
            allStocks.add(info[0]);
            initialSellOrders.add(new Order(OrderType.SELL, info[0], Integer.parseInt(info[1]),
                    Integer.parseInt(info[2]), "broker-0"));
        }
    }

    public MarketInfo getInitialPrices() {
        return initialPrices;
    }

    @Override
    public List<Order> getInitialSellOrders() {
        return this.initialSellOrders;
    }

    @Override
    public Set<String> getAllStocks() {
        return allStocks;
    }
}
