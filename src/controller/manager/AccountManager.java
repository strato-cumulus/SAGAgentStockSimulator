package controller.manager;

import model.Share;
import model.Stock;
import model.account.Account;
import model.account.MasterAccount;
import model.order.BuyOrder;
import model.order.Order;
import resource.data.ShareCreator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AccountManager {

    private final Map<Stock, Set<Account>> stockOwners;
    private final Map<Stock, Set<Account>> stockBuyers;

    public AccountManager() {
        this.stockOwners = new HashMap<>();
        this.stockBuyers = new HashMap<>();
    }

    private final Map<String, Account> accounts = new HashMap<>();

    public void registerAccount(String name) {
        accounts.put(name, new Account());
    }

    public void registerAccount(String name, int funds) {
        accounts.put(name, new Account(funds));
    }

    public Account getAccount(String name) {
        return accounts.get(name);
    }

    public MasterAccount getMasterAccount(ShareCreator shareCreator) {
        MasterAccount masterAccount = new MasterAccount();
        masterAccount.setSharesPerStock(shareCreator.createShares());
        return masterAccount;
    }

    public Map<Stock, Set<Account>> getStockOwners() {
        return stockOwners;
    }

    public Map<Stock, Set<Account>> getStockBuyers() {
        return stockBuyers;
    }
}
