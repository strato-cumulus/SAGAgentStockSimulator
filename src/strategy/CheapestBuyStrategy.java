package strategy;

import jade.core.AID;
import javafx.util.Pair;
import model.Share;
import model.Stock;
import model.order.BuyOrder;
import model.request.PortfolioRequest;

import java.util.*;
import java.util.stream.Collectors;

public class CheapestBuyStrategy extends Strategy {

    CheapestStockComparator comparator = new CheapestStockComparator();

    public Map<AID, BuyOrder> perform(int funds, Map<AID, PortfolioRequest> playerPortfolios) {
        Map<AID, List<Share>> boughtShares = new LinkedHashMap<>();
        List<Pair<AID, List<Share>>> workingList = new ArrayList<>(playerPortfolios.keySet().size());
        for(AID aid: playerPortfolios.keySet()) {
            boughtShares.put(aid, new ArrayList<>());
            List<Share> allStocks = playerPortfolios.get(aid).portfolio.values().stream().flatMap(List::stream).collect(Collectors.toList());
            workingList.add(new Pair<>(aid, allStocks));
        }
        int totalCost = 0;
        while(totalCost < funds) {
            workingList.sort(comparator);
            AID currentPlayer = workingList.get(0).getKey();
            List<Share> currentList = workingList.get(0).getValue();
            Stock currentStock = currentList.get(0).getStock();
            Stock nextStock = currentStock;
            while(nextStock == currentStock && currentList.size() > 0) {
                Share takenShare = currentList.get(0);
                if(totalCost + takenShare.getPrice() > funds) {
                    break;
                }
                currentList.remove(0);
                boughtShares.get(currentPlayer).add(takenShare);
                nextStock = takenShare.getStock();
            }
        }
        return null;
    }

    private class CheapestStockComparator implements Comparator<Pair<AID, List<Share>>> {

        @Override
        public int compare(Pair<AID, List<Share>> o1, Pair<AID, List<Share>> o2) {
            List<Share> l1 = o1.getValue();
            List<Share> l2 = o2.getValue();
            if(l1.size() == 0) {
                return -1;
            }
            if(l2.size() == 0) {
                return 1;
            }
            return Integer.compare(l1.get(0).getPrice(), l2.get(0).getPrice());
        }
    }
}
