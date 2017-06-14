package resource.data;

import model.MarketInfo;
import model.order.Order;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class ShareCreator {

    public abstract void initializeShares() throws IOException;
    public abstract MarketInfo getInitialPrices();
    public abstract List<Order> getInitialSellOrders();
    public abstract Set<String> getAllStocks();
}