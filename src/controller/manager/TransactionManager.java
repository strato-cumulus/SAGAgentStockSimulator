package controller.manager;

import agent.BankAgent;
import agent.util.AgentUtil;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;
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
    private MessageTemplate evalTemplate = MessageTemplate.MatchOntology(Ontology.EVALUATE);

    public TransactionManager (Agent agent, List<SellOrder> sellOrders, List<BuyOrder> buyOrders) {
        this.agent = agent;
        this.sellOrders = sellOrders;
        this.buyOrders = buyOrders;
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
                        //TODO calculate price
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

        List<Integer> demendPrice = new LinkedList<>();;
        demendPrice.addAll(demandMap.keySet());

        Set<Integer> computingSet = new HashSet<Integer>();
        computingSet.addAll(supplyPrice);
        computingSet.addAll(demendPrice);
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
                    return 1;
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
                    return 1;
                }
            }
        }
        return 1;
    }
}
