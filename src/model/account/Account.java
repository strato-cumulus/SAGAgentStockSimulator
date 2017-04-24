package model.account;

import model.Share;
import model.Stock;

import java.util.*;

public class Account {

    protected long funds = 0;
    protected Map<Stock, Set<Share>> sharesPerStock = new HashMap<>();

    public Account() {
    }

    public Account(int funds) {
        this.funds = funds;
    }

    public long getFunds() {
        return this.funds;
    }

    public void setFunds(long funds) {
        this.funds = funds;
    }

    public int getShareAmount(Stock stock) {
        return sharesPerStock.get(stock).size();
    }

    public void putShares(Iterable<Share> shares) {
        for(Share share: shares) {
            Set<Share> stockShares = this.sharesPerStock.computeIfAbsent(share.getStock(), s -> new HashSet<>());
            assert(stockShares != null);
            stockShares.add(share);
        }
    }

    public Iterable<Share> takeShares(Stock stock, int amount) {
        Set<Share> shares = this.sharesPerStock.get(stock);
        assert shares != null && shares.size() >= amount;
        Collection<Share> collectedShares = new ArrayList<>(amount);
        Iterator<Share> shareIterator = shares.iterator();
        for(int a = 0; a < amount; ++a) {
            collectedShares.add(shareIterator.next());
            shareIterator.remove();
        }
        return collectedShares;
    }
}
