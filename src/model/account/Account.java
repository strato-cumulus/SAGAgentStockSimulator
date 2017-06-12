package model.account;

import java.util.*;

public class Account {

    protected int funds = 0;
    protected int clear = 0;
    protected Map<String, Set<String>> sharesPerStock = new HashMap<>();

    public Account() {
    }

    public Account(int funds) {
        this.funds = funds;
        this.clear = funds;
    }

    public boolean blockFunds(int amount) {
        if(amount > clear) return false;
        clear -= amount;
        return true;
    }

    public void unblockFunds(int amount) {
        clear += amount;
    }

    public void subtract(int amount) {
        funds -= amount;
    }

    public int getFunds() {
        return this.funds;
    }

    public void setFunds(int funds) {
        this.funds = funds;
    }

    public int getClear() {
        return clear;
    }

    public void setClear(int clear) {
        this.clear = clear;
    }

    public int getShareAmount(String stock) {
        return sharesPerStock.get(stock).size();
    }

    public void putShares(Iterable<String> shares) {
        for(String stock: shares) {
            Set<String> stocks = this.sharesPerStock.computeIfAbsent(stock, s -> new HashSet<>());
            assert(stocks != null);
            stocks.add(stock);
        }
    }

    public Iterable<String> takeStocks(String stock, int amount) {
        Set<String> shares = this.sharesPerStock.get(stock);
        assert shares != null && shares.size() >= amount;
        Collection<String> collectedStocks = new ArrayList<>(amount);
        Iterator<String> shareIterator = shares.iterator();
        for(int a = 0; a < amount; ++a) {
            collectedStocks.add(shareIterator.next());
            shareIterator.remove();
        }
        return collectedStocks;
    }
}
