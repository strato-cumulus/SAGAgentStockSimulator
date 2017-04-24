package controller.manager;

import model.Share;
import model.Stock;
import model.account.Account;
import model.account.MasterAccount;
import model.order.BuyOrder;
import model.order.Order;
import model.order.SellOrder;

import java.util.*;

public final class OrderManager {

    private static long INITIAL_PRICE = 100L;

    private final AccountManager accountManager;
    private final Map<Stock, SortedSet<BuyOrder>> buyOrders = new HashMap<>();
    private final Map<Stock, SortedSet<SellOrder>> sellOrders = new HashMap<>();

    public OrderManager(AccountManager accountManager) {
        this.accountManager = accountManager;
        MasterAccount masterAccount = accountManager.getMasterAccount();
        for(Map.Entry<Stock, Set<Share>> shareEntry: masterAccount.getSharesPerStock().entrySet()) {
            SortedSet<SellOrder> orders = sellOrders.computeIfAbsent(shareEntry.getKey(),
                    stock -> new TreeSet<>(new Order.ByPriceAscending()));
            orders.add(new SellOrder(masterAccount, shareEntry.getKey(), INITIAL_PRICE, shareEntry.getValue()));
        }
    }

    public synchronized void placeBuyOrder(String agentID, Stock stock, long maxPrice, int desiredAmount) {
        Account buyerAccount = accountManager.getAccount(agentID);
        BuyOrder buyOrder = new BuyOrder(buyerAccount, stock, maxPrice, desiredAmount);

    }

    public synchronized void placeSellOrder(String agentID, Stock stock, Iterable<Share> shares, long askingPrice) {
        Account sellerAccount = accountManager.getAccount(agentID);

    }
}
