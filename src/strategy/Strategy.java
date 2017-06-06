package strategy;

import jade.core.AID;
import model.order.BuyOrder;
import model.request.PortfolioRequest;

import java.util.List;
import java.util.Map;

public abstract class Strategy {

    public static Strategy fromString(String strategyName) {
        System.out.println("Strategia: "+strategyName);
        switch(strategyName) {
            case "CheapestBuy": return new CheapestBuyStrategy();
        }
        return null;
    }

    public abstract Map<AID, List<BuyOrder>> perform(int funds, Map<AID, PortfolioRequest> playerPortfolios);
}
