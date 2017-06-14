package strategy;

import jade.lang.acl.ACLMessage;
import model.messagecontent.Information;
import model.order.*;
import model.request.EquilibriumRequest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InformationResponding extends Strategy {

    @Override
    public Order perform(ACLMessage message, int funds) {
        EquilibriumRequest request = unpack(message);
        String sender = message.getAllReceiver().next().toString();
        if(request.informationList.isEmpty()) {
            return null;
        }
        List<Information> onlyNew = request.informationList
                .stream()
                .filter(i -> i.localDateTime.compareTo(LocalDateTime.now()) >= 0)
                .collect(Collectors.toList());
        onlyNew.sort(Comparator.comparingInt(o -> o.positivity));
        Information worst = onlyNew.get(0);
        Information best = onlyNew.get(onlyNew.size() - 1);
        if(Comparator.<Information>comparingInt(o -> Math.abs(o.positivity)).compare(worst, best) < 0) {
            return new Order(OrderType.SELL, worst.stock, (int)Math.floor(maxPartSpent*funds/request.equilibriumPrice.get(worst.stock)), request.equilibriumPrice.get(worst.stock), sender);
        }
        return new Order(OrderType.BUY, worst.stock, (int)Math.floor(maxPartSpent*funds/request.equilibriumPrice.get(worst.stock)), 1, sender);
    }
}
