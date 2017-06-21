package strategy;

import jade.lang.acl.ACLMessage;
import model.order.Order;
import model.order.OrderType;
import model.request.EquilibriumRequest;
import strategy.util.BiggestRiseComparator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class OnFallBuying extends Strategy {

    private BiggestRiseComparator comparator = new BiggestRiseComparator();

    @Override
    public Order perform(ACLMessage message, int funds, String player,  Map<String, Integer> stocks) {
        EquilibriumRequest request = unpack(message);
        SortedMap<String, List<Integer>> map = new TreeMap<>((s1, s2) ->
                comparator.reverseCompare(request.historicalEquilibriumPrice.get(s1), request.historicalEquilibriumPrice.get(s2)));
        map.putAll(request.historicalEquilibriumPrice);

        Map<String, List<Integer>> targetMap = new HashMap<>();
        targetMap.putAll(request.historicalEquilibriumPrice);

        List<String> keyList = new ArrayList<>(targetMap.keySet());
        int randomIndex = new Random().nextInt(keyList.size());
        String stock = keyList.get(randomIndex);

        double percentToSpend = ThreadLocalRandom.current().nextDouble(0.05, maxPartSpent);
        int price = request.equilibriumPrice.get(stock);
        int randomPrice = ThreadLocalRandom.current().nextInt(price - 100, price + 100);
        int quantity = (int)(percentToSpend * funds / randomPrice);
        if(quantity <= 0) {
            return null;
        }
        return new Order(OrderType.BUY, stock, quantity, randomPrice, player);
    }
}
