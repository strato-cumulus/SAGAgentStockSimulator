package resource.data;

import model.order.Order;
import model.request.EquilibriumRequest;
import resource.ResourceCreationException;

import java.util.List;


public abstract class ShareCreator {

    public abstract void initializeShares() throws ResourceCreationException;
    public abstract EquilibriumRequest getInitialEquilibriumPrices();
    public abstract List<Order> getInitialSellOrders();
    public abstract List<String> getAllStocks();
}