package model.account;

import model.Share;
import model.Stock;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MasterAccount extends Account {

    @Override
    public int getFunds() {
        return Integer.MAX_VALUE;
    }

    public void setFunds(long funds) {
    }

    @Override
    public int getShareAmount(Stock stock) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void putShares(Iterable<Share> shares) {
    }

    @Override
    public Iterable<Share> takeShares(Stock stock, int amount) {
        return IntStream.range(0, amount).mapToObj(o -> new Share(stock, 1)).collect(Collectors.toList());
    }

    public Map<Stock, Set<Share>> getSharesPerStock() {
        return this.sharesPerStock;
    }

    public void setSharesPerStock(Map<Stock, Set<Share>> sharesPerStock) {
        this.sharesPerStock = sharesPerStock;
    }
}
