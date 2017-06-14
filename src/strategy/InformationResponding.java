package strategy;

import jade.lang.acl.ACLMessage;
import model.messagecontent.Information;
import model.order.*;
import model.request.EquilibriumRequest;

import java.util.Comparator;

public class InformationResponding extends Strategy {

    @Override
    public Order perform(ACLMessage message, int funds) {
        EquilibriumRequest request = unpack(message);
        String sender = message.getAllReceiver().next().toString();
        if(request.informationList.isEmpty()) {
            return null;
        }
        request.informationList.sort(Comparator.comparingInt(o -> o.positivity));
        Information worst = request.informationList.get(0);
        Information best = request.informationList.get(request.informationList.size() - 1);
        if(Comparator.<Information>comparingInt(o -> Math.abs(o.positivity)).compare(worst, best) < 0) {
            return new Order(OrderType.SELL, worst.stock, (int)Math.floor(maxPartSpent*funds/request.equilibriumPrice.get(worst.stock)), request.equilibriumPrice.get(worst.stock), sender);
        }
        return new Order(OrderType.BUY, worst.stock, (int)Math.floor(maxPartSpent*funds/request.equilibriumPrice.get(worst.stock)), 1, sender);
    }
}
