package model.request;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class EquilibriumRequest {
    public final Map<String, Integer> equilibriumPrice = new HashMap<String, Integer>();
    public final HashMap<String, LinkedList<Integer>> historicalEquilibriumPrice = new HashMap<String, LinkedList<Integer>>();

    public void addHistoricalPrices(Map<String, Integer> prices) {
        for(String stock: prices.keySet()) {
            LinkedList<Integer> historicalPricesList =  this.historicalEquilibriumPrice.get(stock);
            historicalPricesList.add(prices.get(stock));
            this.historicalEquilibriumPrice.put(stock, historicalPricesList);
        }
    }

    public void updatePrices(Map<String, Integer> newPrices) {
        addHistoricalPrices(this.equilibriumPrice);
        this.equilibriumPrice.clear();
        this.equilibriumPrice.putAll(newPrices);
    }
}
