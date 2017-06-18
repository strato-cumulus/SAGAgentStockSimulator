package model;

import model.messagecontent.Information;

import java.util.*;

public class MarketInfo {
    private Map<String, List<Integer>> pricesHistory = new HashMap<>();
    private Map<String, Integer> prices = new HashMap<>();
    private List<Information> pos = new LinkedList<>();

    public MarketInfo(MarketInfo marketInfo) {
        this.prices.putAll(marketInfo.getCurrPrices());
        this.pricesHistory.putAll(marketInfo.getPricesHistory());
    }

    public MarketInfo(){}

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

    public synchronized void addPositivities(List<Information> informationList) {
        this.pos.clear();
        this.pos.addAll(informationList);
    }

    public synchronized Map<String, List<Integer>> getPricesHistory() {
        return pricesHistory;
    }

    public synchronized Map<String, Integer> getCurrPrices() {
        return prices;
    }

    public synchronized List<Information> getPositivities() {
        return pos;
    }
}
