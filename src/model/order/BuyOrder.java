package model.order;

import jade.core.AID;
import model.Share;
import model.Stock;
import model.account.Account;

import java.util.ArrayList;
import java.util.List;

public class BuyOrder extends Order {

    public BuyOrder(Share share, int quantity, AID buyerAID) {
        super(share, quantity, buyerAID);
    }
}
