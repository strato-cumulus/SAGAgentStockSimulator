package model.order;

import model.Share;
import model.Stock;
import model.account.Account;

import java.util.Collection;

public class SellOrder extends Order {

    public SellOrder(Account account, Stock stock, long unitPrice, Collection<Share> shares) {
        super(account, stock, unitPrice, shares);
    }

    @Override
    public boolean isComplete() {
        return this.shares.size() == 0;
    }
}