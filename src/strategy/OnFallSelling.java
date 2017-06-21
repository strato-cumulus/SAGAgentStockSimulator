package strategy;

import jade.lang.acl.ACLMessage;
import model.order.Order;
import model.order.OrderType;
import model.request.EquilibriumRequest;
import strategy.util.BiggestRiseComparator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OnFallSelling extends Strategy {

    private BiggestRiseComparator comparator = new BiggestRiseComparator();

    @Override
    public Order perform(ACLMessage message, int funds, String player,  Map<String, Integer> stocks) {
        EquilibriumRequest request = unpack(message);
        if(request.equilibriumPrice.size() < 6) {
            return null;
        }
        SortedMap<String, List<Integer>> map = new TreeMap<>((s1, s2) ->
                comparator.reverseCompare(request.historicalEquilibriumPrice.get(s1), request.historicalEquilibriumPrice.get(s2)));
        map.putAll(request.historicalEquilibriumPrice);

        Map<String, List<Integer>> targetMap = new HashMap<>();
        targetMap.putAll(request.historicalEquilibriumPrice);

        List<String> keyList = new ArrayList<>(targetMap.keySet());
        int randomIndex = new Random().nextInt(keyList.size());
        String stock = keyList.get(randomIndex);

        if(stocks.get(stock) != null) {
            double percentOfQuantity = ThreadLocalRandom.current().nextDouble(0.4, maxQuantitySell);
            int quantity = (int) percentOfQuantity * stocks.get(stock);
            int price = request.equilibriumPrice.get(stock);
            int randomPrice = ThreadLocalRandom.current().nextInt(price - 100, price + 100);
            if(quantity <= 0) {
                return null;
            }
            return new Order(OrderType.SELL, stock, quantity, randomPrice, player);
        }
        return null;
    }
}
