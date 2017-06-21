package strategy;

import jade.lang.acl.ACLMessage;
import model.order.Order;
import model.order.OrderType;
import model.request.EquilibriumRequest;
import strategy.util.BiggestRiseComparator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OnRiseBuying extends Strategy {

    BiggestRiseComparator comparator = new BiggestRiseComparator();

    @Override
    public Order perform(ACLMessage message, int funds, String player,  Map<String, Integer> stocks) {
        EquilibriumRequest request = unpack(message);
        if(request.equilibriumPrice.size() < 6) {
            return null;
        }

        SortedMap<String, List<Integer>> map = new TreeMap<>((s1, s2) ->
                comparator.compare(request.historicalEquilibriumPrice.get(s1), request.historicalEquilibriumPrice.get(s2)));
        map.putAll(request.historicalEquilibriumPrice);
        System.out.println("rozmiar PO: " + map.size());
        Map<String, List<Integer>> targetMap = new HashMap<>();


        if(map.get(map.firstKey()).size() > 0) {
            System.out.println("rozmiar mapy: " + map.size());
            int bestDifference = map.get(map.firstKey()).get(0) - map.get(map.firstKey()).get(map.get(map.firstKey()).size() - 1);

            for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
                if(entry.getValue().size() > 0) {
                    if(entry.getValue().get(0) - entry.getValue().get(entry.getValue().size() - 1) == bestDifference) {
                        targetMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            List<String> keyList = new ArrayList<>(targetMap.keySet());
            int randomIndex = new Random().nextInt(keyList.size());
            String stock = keyList.get(randomIndex);
            System.out.println("WYLOSOWANO: " + stock + " RÓŻNICA: " + bestDifference + " KEY SIZE: " + keyList.size());

            double percentToSpend = ThreadLocalRandom.current().nextDouble(0.05, maxPartSpent);
            int price = request.equilibriumPrice.get(stock);
            int randomPrice = ThreadLocalRandom.current().nextInt(price - 100, price + 100);
            int quantity = (int)(percentToSpend * funds / randomPrice);
            if(quantity <= 0) {
                return null;
            }
            return new Order(OrderType.BUY, stock, quantity, randomPrice, player);
        }
        else {
            return null;
        }
    }

}
