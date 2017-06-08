package model;

import java.util.*;

public class Portfolio {

    private final Map<Stock, Integer> amounts = new LinkedHashMap<>();
    private final Map<Stock, Integer> prices = new LinkedHashMap<>();

    public void addStock(Stock stock, int amount, int price) {
        this.amounts.put(stock, amount);
        this.prices.put(stock, price);
    }

    public Integer getAmount(Stock stock) {
        return amounts.get(stock);
    }

    public Integer getPrice(Stock stock) {
        return prices.get(stock);
    }

    public boolean changeAmount(Stock stock, int delta) {
        if(amounts.get(stock) + delta < 0) {
            return false;
        }
        if(amounts.get(stock) + delta == 0) {
            amounts.remove(stock);
            prices.remove(stock);
            return true;
        }
        amounts.put(stock, amounts.get(stock) + delta);
        return true;
    }

    public void copy(Portfolio portfolio) {
        this.amounts.clear();
        this.prices.clear();
        this.amounts.putAll(portfolio.amounts);
        this.prices.putAll(portfolio.prices);
    }

    public List<String> getListOfIndexes() {
        Map <String, Boolean> distinctIndexes = new HashMap<>();
        for (Stock stock : amounts.keySet()) {
            distinctIndexes.put(stock.getTickerCode(), true);
        }
        return new LinkedList<>(distinctIndexes.keySet());
    }

}
