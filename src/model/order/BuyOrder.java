package model.order;

import jade.core.AID;
import model.Stock;

public class BuyOrder extends Order {

    public BuyOrder(Stock stock, int quantity, AID buyerAID) {
        super(stock, quantity, buyerAID);
    }
}
