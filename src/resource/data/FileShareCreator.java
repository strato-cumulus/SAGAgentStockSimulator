package resource.data;

import model.MarketInfo;
import model.order.Order;

import java.io.*;
import java.util.*;

public final class FileShareCreator extends ShareCreator {

    private final String file;
    private final MarketInfo initialMarketInfo = new MarketInfo();

    public FileShareCreator(String file) throws IOException {
        this.file = file;
    }

    @Override
    public void initializeShares() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        while((line = reader.readLine()) != null) {
            if(line.substring(0,1).equals("#")) {
                continue;
            }
            String[] info = line.split(",");
            if (info.length < 3) {
                throw new IOException("Malformed input");
            }
            initialMarketInfo.addPrice(info[0], Integer.parseInt(info[2]));
        }
    }

    public MarketInfo getInitialMarketInfo() {
        return initialMarketInfo;
    }

    @Override
    public List<String> getAllStocks() {
        return new LinkedList<>(initialMarketInfo.getCurrPrices().keySet());
    }
}
