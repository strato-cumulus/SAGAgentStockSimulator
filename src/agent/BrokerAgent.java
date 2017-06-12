package agent;

import agent.util.AgentUtil;
import com.google.gson.Gson;
import controller.manager.TransactionManager;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.Portfolio;
import model.math.Line;
import model.math.Point;
import model.order.BuyOrder;
import model.order.SellOrder;
import model.request.BlockFundsRequest;
import model.request.EquilibriumRequest;
import resource.ResourceCreationException;
import resource.data.FileShareCreator;
import resource.data.ShareCreator;

import java.util.*;

public class BrokerAgent extends Agent {

    public static final AID aid = new AID("broker-0", AID.ISLOCALNAME);

    private ShareCreator shareCreator;
    private int gameDuration;
    private Portfolio portfolio;
    private EquilibriumRequest equilibrium;

    private MessageTemplate equilibriumTemplate = MessageTemplate.MatchOntology(Ontology.EQUILIBRIUM_REQUEST);
    private MessageTemplate portfolioTemplate = MessageTemplate.MatchOntology(Ontology.PORTFOLIO_REQUEST);
    private MessageTemplate buyTemplate = MessageTemplate.MatchOntology(Ontology.BUY_ORDER);
    private MessageTemplate sellTemplate = MessageTemplate.MatchOntology(Ontology.SELL_ORDER);
    private MessageTemplate blockTemplate = MessageTemplate.MatchOntology(Ontology.BLOCK_FUNDS);

    private List<SellOrder> sellOrders;
    private List<BuyOrder> buyOrders = new LinkedList<>();

    private Gson gson = new Gson();


    @Override
    protected void setup() {
        super.setup();
        Object[] args = getArguments();
        if(args != null) {
            gameDuration = Integer.parseInt((String)args[0]);
        } else {
            doDelete();
        }

        initializeShares();

        // add Transaction Manager
        addBehaviour(new TransactionManager(this, sellOrders, buyOrders, equilibrium));

        //EquilibriumRequest
        addBehaviour(new TickerBehaviour(this, 100) {
            private EquilibriumRequest equilibriumRequest;
            private AID senderAID;
            @Override
            public void onTick() {
                ACLMessage message = receive(equilibriumTemplate);
                if(message == null) {
                    block();
                }
                else {
                    senderAID = message.getSender();
                    send(AgentUtil.createMessage(getAID(), equilibrium, ACLMessage.REQUEST, Ontology.EQUILIBRIUM_REQUEST, senderAID));
                    if (getTickCount() > 100) {
                        stop();
                    }
                }
            }
        });

        // Accept a BuyOrder and place it in the queue, pass info to transaction manager to evaluate transactions
        addBehaviour(new Behaviour() {
            private BuyOrder buyOrder;
            @Override
            public void action() {
                ACLMessage message = receive(buyTemplate);
                if(message == null) {
                    block();
                }
                else {
                    buyOrder = gson.fromJson(message.getContent(), BuyOrder.class);
                    int price = buyOrder.getTotalPrice();
                    send(AgentUtil.createMessage(getAID(), new BlockFundsRequest(message.getSender().getName(), price), ACLMessage.REQUEST, Ontology.BLOCK_FUNDS, BankAgent.aid));
                    send(AgentUtil.createMessage(getAID(), "", ACLMessage.PROPAGATE, Ontology.EVALUATE, getAID()));
                    // check for positive/negative response
                    addBehaviour(new Behaviour() {
                        boolean done = false;
                        @Override
                        public void action() {
                            ACLMessage blockResponse = receive(blockTemplate);
                            if(blockResponse == null) block();
                            else {
                                BlockFundsRequest request = gson.fromJson(blockResponse.getContent(), BlockFundsRequest.class);
                                if(request.result) {
                                    buyOrders.add(buyOrder);
                                }
                                done = true;
                            }
                        }

                        @Override
                        public boolean done() {
                            return done;
                        }
                    });
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        // Accept a SellOrder and place it in the queue,  pass info to transaction manager to evaluate transactions
        addBehaviour(new Behaviour() {
            private SellOrder sellOrder;
            @Override
            public void action() {
                ACLMessage message = receive(sellTemplate);
                if(message == null) {
                    block();
                }
                else {
                    sellOrder = gson.fromJson(message.getContent(), SellOrder.class);
                    sellOrders.add(sellOrder);
                    send(AgentUtil.createMessage(getAID(), "", ACLMessage.PROPAGATE, Ontology.EVALUATE, getAID()));
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        addBehaviour(new TickerBehaviour(this, 500) {
            @Override
            protected void onTick() {
                Map<String, Integer> newPrices = new HashMap<String, Integer>();
                for (String stock : equilibrium.equilibriumPrice.keySet()) {
                    newPrices.put(stock, getEquilibriumPrice(stock));
                }
                equilibrium.updatePrices(newPrices);
            }
        });
    }

    protected void initializeShares() {
        try {
            shareCreator = new FileShareCreator("C:\\Users\\filip\\Documents\\GitHub\\SAGAgentStockSimulator\\src\\shares.properties");
            shareCreator.initializeShares();
            equilibrium = shareCreator.getInitialEquilibriumPrices();
            sellOrders = shareCreator.getInitialSellOrders();
        } catch (ResourceCreationException e) {
            e.printStackTrace();
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
            if(buyOrder.getStock() == tickerCode) {
                value = buyOrdersMap.putIfAbsent(buyOrder.getTotalPrice(), buyOrder.getQuantity());
                if (value != null) {
                    buyOrdersMap.put(buyOrder.getTotalPrice(), value + buyOrder.getQuantity());
                }
            }
        }

        for(SellOrder sellOrder : sellOrders) {
            Integer value;
            if(sellOrder.getStock() == tickerCode) {
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


