package model.order;

import model.Share;
import model.Stock;
import model.account.Account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public abstract class Order {

    protected final Stock stock;
    protected final List<Share> shares;

    public Order(Stock stock,Collection<Share> shares) {
        this.stock = stock;
        this.shares = new ArrayList<>(shares.size());
        this.shares.addAll(shares);
    }

    public Stock getStock() {
        return stock;
    }

    public List<Share> getShares() {
        return shares;
    }
}
