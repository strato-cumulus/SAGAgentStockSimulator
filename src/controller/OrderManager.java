package controller;

import model.Account;
import model.Order;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.LongStream;

public final class OrderManager {

    private static long INITIAL_PRICE = 100L;

    private final AccountManager accountManager;
    private final Account account = new Account();
    private final Map<String, SortedSet<Order>> buyOrders = new HashMap<>();
    private final Map<String, SortedSet<Order>> sellOrders = new HashMap<>();

    public OrderManager(AccountManager accountManager) {
        this.accountManager = accountManager;
        Properties definitions = new Properties();
        try {
            definitions.load(new FileInputStream("shares.properties"));
        }
        catch(IOException e) {
            System.out.println("Share definitions not found");
            System.exit(-1);
        }
        for(String propertyName: definitions.stringPropertyNames()) {
            sellOrders.put(propertyName, new TreeSet<>(new Order.ByPriceAscending()));
            SortedSet<Order> stockSellOrders = sellOrders.get(propertyName);
            try {
                long stockCount = Long.parseLong(definitions.getProperty(propertyName));
                LongStream.rangeClosed(1, stockCount)
                    .forEach(i -> stockSellOrders.add(new Order(propertyName, 1, INITIAL_PRICE)));
            }
            catch(NumberFormatException e) {
                System.out.println("Not able to parse share definitions correctly");
                System.exit(-1);
            }
        }
    }

    public synchronized void placeBuyOrder(String agentID, String stock, long count, long price) {

        Account buyerAccount = accountManager.getAccount(agentID);
        Order buyOrder = new Order(buyerAccount, stock, count, price);
        SortedSet<Order> stockSellOrders = sellOrders.get(agentID);
        long purchasedCount = 0L;
        for (Order sellOrder : stockSellOrders) {
            if (sellOrder.price > price || purchasedCount == count) break;
            if(purchasedCount + sellOrder.count > count) {
                stockSellOrders.remove(sellOrder);
                stockSellOrders.add(sellOrder.doTransaction(buyerAccount, count - purchasedCount));
                purchasedCount = count;
            }
            else {
                purchasedCount += sellOrder.count;
                stockSellOrders.remove(sellOrder.doTransaction(buyerAccount));
            }
        }
        if(purchasedCount < count) {
            SortedSet<Order> stockBuyOrders = buyOrders.getOrDefault(stock, new TreeSet<>(new Order.ByPriceDescending()));
            stockBuyOrders.add(buyOrder);
            buyOrders.put(stock, stockBuyOrders);
        }
    }

    public synchronized void placeSellOrder(String agentID, String stock, long count, long price) {

        Account sellerAccount = accountManager.getAccount(agentID);
        Order sellOrder = new Order(sellerAccount, stock, count, price);
        SortedSet<Order> stockBuyOrders = buyOrders.get(stock);
        long soldCount = 0;
        for(Order buyOrder: stockBuyOrders) {
            if(buyOrder.price < price || soldCount == count) break;
            if(soldCount + buyOrder.count > count) {
                stockBuyOrders.remove(buyOrder);
                stockBuyOrders.add(buyOrder.doTransaction(sellerAccount, count - soldCount));
                soldCount = count;
            }
            else {
                soldCount += buyOrder.count;
                stockBuyOrders.remove(buyOrder.doTransaction(sellerAccount));
            }
        }
        if(soldCount < count) {
            SortedSet<Order> stockSellOrders = sellOrders.getOrDefault(stock, new TreeSet<>(new Order.ByPriceAscending()));
            stockBuyOrders.add(sellOrder);
            sellOrders.put(stock, stockSellOrders);
        }
    }
}
