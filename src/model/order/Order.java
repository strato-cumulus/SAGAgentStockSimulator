package model.order;

import model.Share;
import model.Stock;
import model.account.Account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public abstract class Order {

    protected final Account account;
    protected final Stock stock;
    protected final long unitPrice;
    protected final List<Share> shares;

    public Order(Account account, Stock stock, long unitPrice, Collection<Share> shares) {
        this.account = account;
        this.stock = stock;
        this.unitPrice = unitPrice;
        this.shares = new ArrayList<>(shares.size());
        this.shares.addAll(shares);
    }

    public abstract boolean isComplete();

    public static class ByPriceAscending implements Comparator<Order> {

        @Override
        public int compare(Order o1, Order o2) {
            if(o1.unitPrice == o2.unitPrice) {
                return 0;
            }
            return o1.unitPrice < o2.unitPrice ? 1: -1;
        }
    }

    public static class ByPriceDescending implements Comparator<Order> {

        @Override
        public int compare(Order o1, Order o2) {
            if(o1.unitPrice == o2.unitPrice) {
                return 0;
            }
            return o1.unitPrice > o2.unitPrice ? 1: -1;
        }
    }
}
