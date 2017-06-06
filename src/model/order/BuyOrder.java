package model.order;

import jade.core.AID;
import model.Stock;

public class BuyOrder extends Order {

    public BuyOrder(Stock stock, int quantity, String buyerName) {
        super(stock, quantity, buyerName);
    }
}
