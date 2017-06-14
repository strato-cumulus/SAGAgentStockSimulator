package strategy;

import jade.lang.acl.ACLMessage;
import model.order.Order;
import model.order.OrderType;
import model.request.EquilibriumRequest;
import strategy.util.BiggestRiseComparator;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class OnFallBuying extends Strategy {

    private BiggestRiseComparator comparator = new BiggestRiseComparator();

    @Override
    public Order perform(ACLMessage message, int funds) {
        EquilibriumRequest request = unpack(message);
        //TODO fix this
//        SortedMap<String, List<Integer>> map = new TreeMap<>((s1, s2) ->
//                comparator.reverseCompare(request.historicalEquilibriumPrice.get(s1), request.historicalEquilibriumPrice.get(s2)));
        SortedMap<String, List<Integer>> map = new TreeMap<>();
        map.putAll(request.historicalEquilibriumPrice);
        return new Order(OrderType.BUY, map.firstKey(), (int)Math.floor(maxPartSpent*funds/request.equilibriumPrice.get(map.firstKey())), 1, message.getAllReceiver().next().toString());
    }
}
