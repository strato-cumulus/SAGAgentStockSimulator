package model.request;

import model.messagecontent.Information;

import java.util.*;

public class EquilibriumRequest {
    public final Map<String, Integer> equilibriumPrice = new HashMap<>();
    public final Map<String, List<Integer>> historicalEquilibriumPrice = new HashMap<>();
    public final List<Information> informationList = new ArrayList<>();

    public void addHistoricalPrices(Map<String, Integer> prices) {
        for(String stock: prices.keySet()) {
            List<Integer> historicalPricesList =  this.historicalEquilibriumPrice.get(stock);
            if(historicalPricesList == null)
                historicalPricesList = new LinkedList<Integer>();
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