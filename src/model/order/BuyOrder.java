package model.order;

import model.Stock;
import model.account.Account;

import java.util.ArrayList;

public class BuyOrder extends Order {

    private final int desiredAmount;

    public BuyOrder(Account account, Stock stock, long unitPrice, int desiredAmount) {
        super(account, stock, unitPrice, new ArrayList<>(desiredAmount));
        this.desiredAmount = desiredAmount;
    }

    @Override
    public boolean isComplete() {
        return this.shares.size() == desiredAmount;
    }
}
