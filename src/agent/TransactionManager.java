package agent;

import agent.util.AgentUtil;
import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.math.Line;
import model.math.Point;
import model.order.Order;
import model.order.OrderType;
import model.transaction.Transaction;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Marcin on 14.06.2017.
 */
public class TransactionManager extends Agent {

    private final List<Order> sellOrders = new LinkedList<>();
    private final List<Order> buyOrders = new LinkedList<>();
    private MessageTemplate orderTemplate = MessageTemplate.MatchOntology(Ontology.HANDLE_ORDER);
    private Gson gson = new Gson();
    private String tickerId;
    private Integer initialAmount;
    private Integer initialPrice;
    private AID brokerAID;
    private Transaction currentTransaction;
    private Integer equilibriumPrice;


    @Override
    protected void setup() {
        super.setup();
        initialAmount = Integer.parseInt((String)getArguments()[0]);
        initialPrice = Integer.parseInt((String)getArguments()[1]);
        tickerId = getArguments()[2].toString();
        brokerAID = BrokerAgent.aid;
        equilibriumPrice = initialPrice;

        //obsługa orderów
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(orderTemplate);
                if (message == null) {
                    block();
                } else {
                    Order order = gson.fromJson(message.getContent(), Order.class);
                    order.setArrivalTime(LocalDateTime.now());
                    if(order.type == OrderType.BUY) {
                        if (tradeBuyOrder(order) == true) {
                            send(AgentUtil.createMessage(getAID(), currentTransaction, ACLMessage.REQUEST, Ontology.TRANSACTION, BrokerAgent.aid));
                        }
                    }
                    if (order.type == OrderType.SELL) {
                        if (tradeSellOrder(order) == true) {
                            send(AgentUtil.createMessage(getAID(), currentTransaction, ACLMessage.REQUEST, Ontology.TRANSACTION, BrokerAgent.aid));
                        }
                    }
                }
            }
        });

        //wysyłanie aktualnego kursu do brokera
        addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            public void onTick() {
                Integer price = getEquilibriumPrice();
                if(price > 0) {
                    equilibriumPrice = price;
                }
                send(AgentUtil.createMessage(getAID(), equilibriumPrice,  ACLMessage.INFORM, Ontology.EQUILIBRIUM_INFO, brokerAID));
            }
        });
    }



    @Override
    public void addBehaviour(Behaviour b) {
        super.addBehaviour(b);
    }

    //sprawdza i jeżeli może wykonuje transakcję - jeżeli dokonano transakcji zwraca 1, jeżeli nie to odkłada order do kolejki i zwraca 0
    private boolean tradeBuyOrder(Order buyOrder) {
        String buyerName;
        String sellerName;
        String stock;
        int transactionQuantity;
        int transactionPrice;
        int offerPrice = buyOrder.unitPrice;
        int initialQuantity = buyOrder.getQuantity();
        for (Order sellOrder : sellOrders) {
            if(sellOrder.unitPrice <= buyOrder.unitPrice) {
                transactionPrice = sellOrder.unitPrice;
                buyerName = buyOrder.playerName;
                sellerName = sellOrder.playerName;
                stock = sellOrder.stock;
                if(sellOrder.getQuantity() == buyOrder.getQuantity()) {
                    transactionQuantity = buyOrder.getQuantity();
                    currentTransaction = new Transaction(buyerName, sellerName, stock, transactionQuantity, transactionPrice, offerPrice);
                    sellOrder.setQuantity(0);
                    buyOrder.setQuantity(0);
                    break;
                }
                else if (sellOrder.getQuantity() > buyOrder.getQuantity()) {
                    transactionQuantity = buyOrder.getQuantity();
                    currentTransaction = new Transaction(buyerName, sellerName, stock, transactionQuantity, transactionPrice, offerPrice);
                    sellOrder.setQuantity(sellOrder.getQuantity() - transactionQuantity);
                    buyOrder.setQuantity(0);
                    break;
                }
                else {
                    transactionQuantity = sellOrder.getQuantity();
                    currentTransaction = new Transaction(buyerName, sellerName, stock, transactionQuantity, transactionPrice, offerPrice);
                    sellOrders.remove(sellOrder);
                    sellOrder.setQuantity(0);
                    buyOrder.setQuantity(buyOrder.getQuantity() - transactionQuantity);
                }
            }
        }
        Iterator<Order> iterator = sellOrders.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().getQuantity() == 0) {
                iterator.remove();
            }
        }
        if(buyOrder.getQuantity() > 0 && buyOrder.getQuantity() < initialQuantity) {
            buyOrders.add(buyOrder);
            buyOrders.sort(Order.BuyOrderComparator);
            return true;
        }
        if(buyOrder.getQuantity() == 0) {
            return true;
        }
        if(buyOrder.getQuantity() == initialQuantity){
            return false;
        }
        return false;
    }

    //sprawdza i jeżeli może wykonuje transakcję - jeżeli dokonano transakcji zwraca 1, jeżeli nie to odkłada order do kolejki i zwraca 0
    private boolean tradeSellOrder(Order sellOrder) {
        String buyerName;
        String sellerName;
        String stock;
        int transactionQuantity;
        int transactionPrice;
        int offerPrice;
        int initialQuantity = sellOrder.getQuantity();
        for (Order buyOrder : buyOrders) {
            if(buyOrder.unitPrice >= sellOrder.unitPrice) {
                transactionPrice = sellOrder.unitPrice;
                buyerName = buyOrder.playerName;
                sellerName = sellOrder.playerName;
                stock = sellOrder.stock;
                offerPrice = buyOrder.unitPrice;
                if(buyOrder.getQuantity() == sellOrder.getQuantity()) {
                    transactionQuantity = sellOrder.getQuantity();
                    currentTransaction = new Transaction(buyerName, sellerName, stock, transactionQuantity, transactionPrice, offerPrice);
                    buyOrder.setQuantity(0);
                    sellOrder.setQuantity(0);
                    break;
                }
                else if(buyOrder.getQuantity() > sellOrder.getQuantity()) {
                    transactionQuantity = sellOrder.getQuantity();
                    currentTransaction = new Transaction(buyerName, sellerName, stock, transactionQuantity, transactionPrice, offerPrice);
                    buyOrder.setQuantity(buyOrder.getQuantity() - transactionQuantity);
                    sellOrder.setQuantity(0);
                    break;
                }
                else {
                    transactionQuantity = buyOrder.getQuantity();
                    currentTransaction = new Transaction(buyerName, sellerName, stock, transactionQuantity, transactionPrice, offerPrice);
                    buyOrder.setQuantity(0);
                    sellOrder.setQuantity(sellOrder.getQuantity() - transactionQuantity);
                }
            }
        }
        Iterator<Order> iterator = buyOrders.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().getQuantity() == 0) {
                iterator.remove();
            }
        }
        if(sellOrder.getQuantity() > 0 && sellOrder.getQuantity() < initialQuantity) {
            sellOrders.add(sellOrder);
            sellOrders.sort(Order.SellOrderComparator);
            return true;
        }
        if(sellOrder.getQuantity() == 0) {
            return true;
        }
        if(sellOrder.getQuantity() == initialQuantity){
            return false;
        }
        return false;
    }


    private int getEquilibriumPrice() {
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

        if(buyOrders.size() == 0 && sellOrders.size() == 0) {
            return 0;
        }

        for(Order buyOrder : buyOrders) {
            Integer value;
            value = buyOrdersMap.putIfAbsent(buyOrder.unitPrice, buyOrder.getQuantity());
            if (value != null) {
                buyOrdersMap.put(buyOrder.unitPrice, value + buyOrder.getQuantity());
            }
        }

        for(Order sellOrder : sellOrders) {
            Integer value;
            value = sellOrdersMap.putIfAbsent(sellOrder.unitPrice, sellOrder.getQuantity());
            if (value != null) {
                sellOrdersMap.put(sellOrder.unitPrice, value + sellOrder.getQuantity());
            }
        }

        if(buyOrdersMap.size() == 0) {
            List<Integer> multiplication = new LinkedList<>();
            for(Integer key : sellOrdersMap.keySet()) {
                multiplication.add(key * sellOrdersMap.get(key));
            }
            return  multiplication.stream().reduce(0, Integer::sum)/sellOrdersMap.values().stream().reduce(0, Integer::sum);
        }

        if(sellOrdersMap.size() == 0) {
            List<Integer> multiplication = new LinkedList<>();
            for(Integer key : buyOrdersMap.keySet()) {
                multiplication.add(key * buyOrdersMap.get(key));
            }
            return  multiplication.stream().reduce(0, Integer::sum)/buyOrdersMap.values().stream().reduce(0, Integer::sum);
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
