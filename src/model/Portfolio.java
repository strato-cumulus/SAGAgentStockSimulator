package model;

import java.util.*;

public class Portfolio {

    private final Map<String, Integer> amounts = new LinkedHashMap<>();
    private final Map<String, Integer> prices = new LinkedHashMap<>();

    public void addStock(String stock, int amount, int price) {
        this.amounts.put(stock, amount);
        this.prices.put(stock, price);
    }

    public Integer getAmount(String stock) {
        return amounts.get(stock);
    }

    public Integer getPrice(String stock) {
        return prices.get(stock);
    }

    public boolean changeAmount(String stock, int delta) {
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
        for (String stock : amounts.keySet()) {
            distinctIndexes.put(stock, true);
        }
        return new LinkedList<>(distinctIndexes.keySet());
    }

    public Map<String, Integer> getInitialEquilibriumPrice() {
        return this.prices;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Portfolio: \n");
        for (String stock : amounts.keySet()) {
            sb.append("Stock: " + stock + " amount: " + amounts.get(stock).toString() + " price: " + prices.get(stock).toString() + "\n");
        }
        return sb.toString();
    }
}
