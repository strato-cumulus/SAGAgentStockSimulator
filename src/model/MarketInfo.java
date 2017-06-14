package model;

import model.messagecontent.Information;

import java.util.*;

/**
 * Created by Marcin on 14.06.2017.
 */
public class MarketInfo {
    private Map<String, List<Integer>> pricesHistory = new HashMap<>();
    private Map<String, Integer> prices = new HashMap<>();
    private List<Information> positivities = new ArrayList<>();

    public synchronized void addPrice(String stock, Integer price) {
        prices.put(stock, price);
        List<Integer> oneStockHistory = pricesHistory.get(stock);
        if(oneStockHistory != null) {
            oneStockHistory.add(price);
        }
        else {
            List<Integer> newStock = new LinkedList<>();
            newStock.add(price);
            pricesHistory.put(stock, newStock);
        }
    }

    public synchronized void addPositivities(List<Information> information) {
        positivities.addAll(information);
    }

    public synchronized Map<String, List<Integer>> getPricesHisotry() {
        return pricesHistory;
    }

    public synchronized Map<String, Integer> getCurrPrices() {
        return prices;
    }

    public synchronized List<Information> getPositivities() {
        return positivities;
    }

    public synchronized void updatePrices(Map<String, Integer> prices) {
        prices = new HashMap<>();
        prices.putAll(prices);
    }
}
