package strategy;

import jade.core.AID;
import model.Share;
import model.Stock;
import model.order.BuyOrder;
import model.request.PortfolioRequest;

import java.util.List;
import java.util.Map;

public abstract class Strategy {

    public static Strategy fromString(String strategyName) {
        switch(strategyName) {
            case "CheapestBuy": return new CheapestBuyStrategy();
        }
        return null;
    }

    public abstract Map<AID, BuyOrder> perform(int funds, Map<AID, PortfolioRequest> playerPortfolios);
}
