package model;

import java.util.*;

public class Account {

    private long funds = 0;
    private Map<String, Long> stocks = new HashMap<>();

    public Account() {
    }

    public Account(long funds) {
        this.funds = funds;
    }

    boolean subtractFunds(long amount) {
        if(funds < amount) {
            return false;
        }
        funds -= amount;
        return true;
    }

    void addFunds(long amount) {
        funds += amount;
    }

    boolean checkPurchase(Order order) {
        return order.count * order.price <= funds;
    }

    boolean checkSell(Order order) {
        return stocks.getOrDefault(order.stock, 0L) >= order.count;
    }

    void doPurchase(Order order) {
        stocks.put(order.stock, order.count + stocks.getOrDefault(order.stock, 0L));
    }

    void doSell(Order order) {
        stocks.put(order.stock, stocks.getOrDefault(order.stock, 0L) - order.count);
        funds += order.count * order.price;
    }
}
