package strategy;

import jade.lang.acl.ACLMessage;
import model.messagecontent.Information;
import model.order.*;
import model.request.EquilibriumRequest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class InformationResponding extends Strategy {

    @Override
    public Order perform(ACLMessage message, int funds, String player, Map<String, Integer> stocks) {
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
            double percentOfQuantity = ThreadLocalRandom.current().nextDouble(0.4, maxQuantitySell);
            int quantity = (int) percentOfQuantity * stocks.get(worst.stock);
            int price = request.equilibriumPrice.get(worst.stock);
            int randomPrice = ThreadLocalRandom.current().nextInt(price - 100, price + 100);
            if(quantity <= 0) {
                return null;
            }
            return new Order(OrderType.SELL, worst.stock, quantity, randomPrice, player);
        }
        double percentToSpend = ThreadLocalRandom.current().nextDouble(0.05, maxPartSpent);
        int price = request.equilibriumPrice.get(best.stock);
        int randomPrice = ThreadLocalRandom.current().nextInt(price - 100, price + 100);
        int quantity = (int)(percentToSpend * funds / randomPrice);
        if(quantity <= 0) {
            return null;
        }
        return new Order(OrderType.BUY, best.stock, quantity, randomPrice, player);
    }
}
