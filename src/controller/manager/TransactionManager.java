package controller.manager;

import agent.BankAgent;
import agent.util.AgentUtil;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;
import model.math.Line;
import model.math.Point;
import model.Ontology;
import model.order.BuyOrder;
import model.order.SellOrder;
import model.request.CommitTransactionRequest;
import model.transaction.Transaction;

import java.util.*;

public class TransactionManager extends CyclicBehaviour {


    private Agent agent;
    private List<SellOrder> sellOrders;
    private List<BuyOrder> buyOrders;
    private List<Transaction> transactions;
    private List<String> indexesList;
    private MessageTemplate evalTemplate = MessageTemplate.MatchOntology(Ontology.EVALUATE);

    public TransactionManager (Agent agent, List<SellOrder> sellOrders, List<BuyOrder> buyOrders, List<String> indexesList) {
        this.agent = agent;
        this.sellOrders = sellOrders;
        this.buyOrders = buyOrders;
        this.indexesList = indexesList;
    }

    @Override
    public void action() {
        ACLMessage message = agent.receive(evalTemplate);
        if (message == null) {
            block();
        } else {
            buyOrderLoop:
            for(BuyOrder buyOrder: buyOrders) {
                for(SellOrder sellOrder: sellOrders) {
                    if(buyOrder.getStock().equals(sellOrder.getStock()) && buyOrder.getUnitPrice() >= sellOrder.getUnitPrice()) {
                        int transactionQuantity = buyOrder.getQuantity() > sellOrder.getQuantity() ? buyOrder.getQuantity() : sellOrder.getQuantity();
                        int transactionUnitPrice = sellOrder.getUnitPrice();
                        transactions.add(new Transaction(buyOrder.getPlayerName(),
                                                         sellOrder.getPlayerName(),
                                                         buyOrder.getStock(),
                                                         transactionQuantity,
                                                         transactionUnitPrice
                                                         ));
                        buyOrder.setQuantity(buyOrder.getQuantity()-transactionQuantity);
                        sellOrder.setQuantity(sellOrder.getQuantity()-transactionQuantity);
                        if(buyOrder.getQuantity()==0) buyOrders.remove(buyOrder);
                        if(sellOrder.getQuantity()==0) sellOrders.remove(sellOrder);
                        CommitTransactionRequest commitTransactionRequest = new CommitTransactionRequest(buyOrder.getPlayerName(), transactionUnitPrice*transactionQuantity);
                        agent.send(AgentUtil.createMessage(agent.getAID(), commitTransactionRequest, ACLMessage.REQUEST, Ontology.COMMIT_TRANSACTION, BankAgent.aid));

                        //TODO calculate price - coś sprytniejszego trzeba - liczenie co jakiś odstęp czasu? Żeby łatwiejsza analiza była?
                        for (String tickerCode : indexesList) {
                            getEquilibriumPrice(tickerCode);
                        }
                        break buyOrderLoop;
                    }
                }
            }
        }
    }

    private int getEquilibriumPrice(String tickerCode) {
        // <cena, ilosc>
        SortedMap<Integer, Integer> buyOrdersMap = new TreeMap<>(Collections.reverseOrder());
        SortedMap<Integer, Integer> sellOrdersMap = new TreeMap<>();
        Map<Integer, Integer> supplyMap = new HashMap<>();
        Map<Integer, Integer> demandMap = new HashMap<>();
        Point A1 = new Point();
        Point B1 = new Point();
        Point A2 = new Point();
        Point B2 = new Point();
        Integer distribution = 0;

        for(BuyOrder buyOrder : buyOrders) {
            Integer value;
            if(buyOrder.getStock().getTickerCode() == tickerCode) {
                value = buyOrdersMap.putIfAbsent(buyOrder.getTotalPrice(), buyOrder.getQuantity());
                if (value != null) {
                    buyOrdersMap.put(buyOrder.getTotalPrice(), value + buyOrder.getQuantity());
                }
            }
        }

        for(SellOrder sellOrder : sellOrders) {
            Integer value;
            if(sellOrder.getStock().getTickerCode() == tickerCode) {
                value = sellOrdersMap.putIfAbsent(sellOrder.getTotalPrice(), sellOrder.getQuantity());
                if (value != null) {
                    sellOrdersMap.put(sellOrder.getTotalPrice(), value + sellOrder.getQuantity());
                }
            }
        }

        for(Map.Entry<Integer,Integer> entry : sellOrdersMap.entrySet()) {
            distribution = distribution + distribution + entry.getValue();
            supplyMap.put(entry.getKey(),distribution);
        }

        for(Map.Entry<Integer,Integer> entry : buyOrdersMap.entrySet()) {
            distribution = distribution + distribution + entry.getValue();
            supplyMap.put(entry.getKey(),distribution);
        }

        List<Integer> supplyPrice = new LinkedList<>();
        supplyPrice.addAll(supplyMap.keySet());

        List<Integer> demandPrice = new LinkedList<>();
        demandPrice.addAll(demandMap.keySet());

        Set<Integer> computingSet = new HashSet<>();
        computingSet.addAll(supplyPrice);
        computingSet.addAll(demandPrice);
        Set<Integer> sortedSet = new TreeSet<>();
        sortedSet.addAll(computingSet);

        List<Integer> sortedList = new ArrayList<>(sortedSet);

        for(int i = 0; i < sortedList.size(); i++) {
            Integer supplyQuantity = supplyMap.get(sortedList.get(i));
            if(supplyQuantity == null) continue;
            Integer demandQuantity = demandMap.get(sortedList.get(i));
            if(demandQuantity == null) demandQuantity = Integer.MAX_VALUE;

            if(supplyQuantity == demandQuantity) {
                return sortedList.get(i);
            }

            if(supplyQuantity < demandQuantity) {
                int j;
                int k;
                for (j = i+1; supplyMap.get(sortedList.get(j)) == null && j < sortedList.size(); j++);
                for (k = i+1; demandMap.get(sortedList.get(k)) == null && k < sortedList.size(); k++);
                Integer nextSupplyQuantity = supplyMap.get(sortedList.get(j));
                Integer nextDemandQuantity = demandMap.get(sortedList.get(k));
                if(nextDemandQuantity < nextSupplyQuantity) {
                    A1 = new Point(supplyQuantity.doubleValue(), sortedList.get(i).doubleValue());
                    B1 = new Point(nextSupplyQuantity.doubleValue(), sortedList.get(j).doubleValue());
                }
            }
        }

        for(int i = sortedList.size() - 1; i >= 0; i--) {
            Integer supplyQuantity = supplyMap.get(sortedList.get(i));
            if(supplyQuantity == null) supplyQuantity = Integer.MAX_VALUE;;
            Integer demandQuantity = demandMap.get(sortedList.get(i));
            if(demandQuantity == null) continue;

            if(demandQuantity < supplyQuantity) {
                int j;
                int k;
                for (j = i-1; supplyMap.get(sortedList.get(j)) == null && j >= 0; j--);
                for (k = i-1; demandMap.get(sortedList.get(k)) == null && k >= 0; k--);
                Integer nextSupplyQuantity = supplyMap.get(sortedList.get(j));
                Integer nextDemandQuantity = demandMap.get(sortedList.get(k));
                if(nextDemandQuantity > nextSupplyQuantity) {
                    A2 = new Point(demandQuantity.doubleValue(), sortedList.get(i).doubleValue());
                    B2 = new Point(nextDemandQuantity.doubleValue(), sortedList.get(k).doubleValue());
                }
            }
        }
        return intersection(A1, B1, A2, B2);
    }

    private int intersection (Point A1, Point B1, Point A2, Point B2 ) {
        Line LA = Line.getLineFromPoints(A1, B1);
        Line LB = Line.getLineFromPoints(A2, B2);
        return LA.getIntersection(LB).getY().intValue();
    }
}
