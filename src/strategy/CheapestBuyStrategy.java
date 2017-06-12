package strategy;

import jade.core.AID;
import javafx.util.Pair;
import model.Portfolio;
import model.order.BuyOrder;
import model.request.PortfolioRequest;

import java.util.*;

public class CheapestBuyStrategy extends Strategy {


    public Map<AID, List<BuyOrder>> perform(int funds, Map<AID, PortfolioRequest> playerPortfolios) {
        Map<AID, Portfolio> boughtShares = new LinkedHashMap<>();
        List<Pair<AID, Portfolio>> workingList = new ArrayList<>(playerPortfolios.keySet().size());
        for(AID aid: playerPortfolios.keySet()) {
            //boughtShares.put(aid, new ArrayList<>());
            //
            //workingList.add(new Pair<>(aid, allStocks));
        }
        int totalCost = 0;
        while(totalCost < funds) {
            //workingList.sort(comparator);
            AID currentPlayer = workingList.get(0).getKey();
            Portfolio currentPortfolio = workingList.get(0).getValue();
            //
        }
        Map<AID, List<BuyOrder>> buyOrders = new HashMap<>();
        for(Map.Entry<AID, Portfolio> shareEntry: boughtShares.entrySet()) {
            Portfolio shares = shareEntry.getValue();
            Map<String, Portfolio> sharesPerStock = new HashMap<>();
            //
        }
        return buyOrders;
    }

}
