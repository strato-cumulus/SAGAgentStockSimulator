package strategy;

import jade.lang.acl.ACLMessage;
import model.order.BuyOrder;
import model.order.Order;
import model.request.EquilibriumRequest;
import strategy.util.BiggestRiseComparator;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class OnRiseBuying extends Strategy {

    BiggestRiseComparator comparator = new BiggestRiseComparator();

    @Override
    public Order perform(ACLMessage message, int funds) {
        EquilibriumRequest request = unpack(message);
        SortedMap<String, List<Integer>> map = new TreeMap<>((s1, s2) ->
                comparator.compare(request.historicalEquilibriumPrice.get(s1), request.historicalEquilibriumPrice.get(s2)));
        return new BuyOrder(map.firstKey(), (int)Math.floor(maxPartSpent*funds/request.equilibriumPrice.get(map.firstKey())), message.getAllReceiver().next().toString());
    }

}
