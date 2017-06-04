package model.order;

import model.Share;
import model.Stock;
import model.account.Account;

import java.util.ArrayList;
import java.util.List;

public class BuyOrder extends Order {

    public BuyOrder(Stock stock, List<Share> shares) {
        super(stock, shares);
    }
}
