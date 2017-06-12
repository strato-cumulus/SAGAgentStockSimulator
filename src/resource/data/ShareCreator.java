package resource.data;

import model.order.SellOrder;
import model.request.EquilibriumRequest;
import resource.ResourceCreationException;

import java.util.List;


public abstract class ShareCreator {

    public abstract void initializeShares() throws ResourceCreationException;
    public abstract EquilibriumRequest getInitialEquilibriumPrices();
    public abstract List<SellOrder> getInitialSellOrders();
}