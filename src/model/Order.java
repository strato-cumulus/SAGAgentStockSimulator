package model;

import java.util.Comparator;

public class Order {

    public final Account account;
    public final String stock;
    public long count;
    public final long price;

    // A global order with no benefactor(useful for initial distribution of shares)
    public Order(String stock, long count, long price) {
        this(null, stock, count, price);
    }

    public Order(Account account, String stock, long count, long price) {
        this.account = account;
        this.stock = stock;
        this.count = count;
        this.price = price;
    }

    private Order(Order order, long partialCount) {
        this.account = order.account;
        this.stock = order.stock;
        this.count = partialCount;
        this.price = order.price;
    }

    public Order doTransaction(Account buyerAccount, long partialCount) {
        Order partialOrder = new Order(this, partialCount);
        partialOrder.doTransaction(buyerAccount);
        return new Order(this, this.count - partialCount);
    }

    public Order doTransaction(Account buyerAccount) {
        if(account == null) return this;
        if(!(account.checkSell(this) && account.checkPurchase(this))) return null;
        account.doSell(this);
        buyerAccount.doPurchase(this);
        return this;
    }

    public static class ByPriceAscending implements Comparator<Order> {

        @Override
        public int compare(Order o1, Order o2) {
            if(o1.price == o2.price) {
                return 0;
            }
            return o1.price < o2.price? 1: -1;
        }
    }

    public static class ByPriceDescending implements Comparator<Order> {

        @Override
        public int compare(Order o1, Order o2) {
            if(o1.price == o2.price) {
                return 0;
            }
            return o1.price > o2.price? 1: -1;
        }
    }
}
