package resource.data;

import model.order.Order;
import model.order.OrderType;
import model.request.EquilibriumRequest;
import resource.ResourceCreationException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public final class FileShareCreator extends ShareCreator {

    private final String file;
    private final List<String> allStocks = new ArrayList<>();
    private final EquilibriumRequest initialPrices = new EquilibriumRequest();
    private final List<Order> initialSellOrders = new LinkedList<>();

    public FileShareCreator(String file) throws ResourceCreationException {
        this.file = file;
    }

    @Override
    public void initializeShares() throws ResourceCreationException {
        Properties fileStockDefinitions = new Properties();
        try {
            fileStockDefinitions.load(new InputStreamReader(new FileInputStream(file)));
        }
        catch (IOException e) {
            throw new ResourceCreationException((e instanceof FileNotFoundException)?"File not found":"Malformed input");
        }
        Set<String> tickerCodes = fileStockDefinitions.stringPropertyNames();
        allStocks.addAll(tickerCodes);
        for(String tickerCode: tickerCodes) {
            String[] shareData = fileStockDefinitions.getProperty(tickerCode).split(";");
            try {
                int amount = Integer.parseInt(shareData[0]);
                int price = Integer.parseInt(shareData[1]);
                initialPrices.equilibriumPrice.put(tickerCode, price);
                initialSellOrders.add(new Order(OrderType.SELL, tickerCode, amount, price, new String("broker-0")));
            }
            catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new ResourceCreationException("Malformed input");
            }
        }
    }

    @Override
    public EquilibriumRequest getInitialEquilibriumPrices() {
        return this.initialPrices;
    }

    @Override
    public List<Order> getInitialSellOrders() {
        return this.initialSellOrders;
    }

    @Override
    public List<String> getAllStocks() {
        return allStocks;
    }
}
