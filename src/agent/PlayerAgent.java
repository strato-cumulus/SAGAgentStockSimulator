package agent;

import agent.util.AgentUtil;
import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.order.Order;
import model.request.*;
import strategy.Strategy;

import java.util.HashMap;
import java.util.Map;

public class PlayerAgent extends Agent {

    private Gson gson = new Gson();
    private AID bankAID;
    private AID brokerAID;
    private Strategy buyStrategy;
    private Strategy sellStrategy;
    private boolean readyToTrade;
    private int funds;
    private int clear;
    private Order sellOrder;
    private Order buyOrder;
    private final Map<String, Integer> stocks = new HashMap<>();
    private MessageTemplate equilibriumResTemplate = MessageTemplate.MatchOntology(Ontology.EQUILIBRIUM_RESPONSE);
    private final MessageTemplate checkFundsTemplate = MessageTemplate.MatchOntology(Ontology.CHECK_FUNDS);
    private final MessageTemplate addStocksTemplate = MessageTemplate.MatchOntology(Ontology.ADD_STOCKS);

    @Override
    protected void setup() {
        super.setup();
        bankAID = BankAgent.aid;
        brokerAID = BrokerAgent.aid;
        buyStrategy = Strategy.fromString((String) getArguments()[0]);
        sellStrategy = Strategy.fromString((String) getArguments()[1]);
        readyToTrade = false;

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                AddAccountRequest accountRequest = new AddAccountRequest(this.getAgent().getAID().getLocalName(), Integer.parseInt((String) getArguments()[2]));
                send(AgentUtil.createMessage(getAID(), accountRequest,  ACLMessage.REQUEST, Ontology.ADD_ACCOUNT, bankAID));
                send(AgentUtil.createMessage(getAID(), getAID().getLocalName(), ACLMessage.REQUEST, Ontology.REGISTER_REQUEST, brokerAID));
            }
        });

        //EquilibriumRequest
        addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            public void onTick() {
                ACLMessage queryMessage = AgentUtil.createMessage(getAID(), new EquilibriumRequest(),  ACLMessage.REQUEST, Ontology.EQUILIBRIUM_REQUEST, brokerAID);
                send(queryMessage);
                addBehaviour(new Behaviour() {
                    boolean done = false;
                    @Override
                    public void action() {
                        ACLMessage response = receive(equilibriumResTemplate);
                        if(response == null) block();
                        else {
                            buyOrder = buyStrategy.perform(response, clear);
                            sellOrder = sellStrategy.perform(response, clear);
                            readyToTrade = true;
                            done = true;
                        }
                    }

                    @Override
                    public boolean done() {
                        return done;
                    }

                });
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                if (readyToTrade == true) {
                    System.out.println("ReadyToTrade");
                    ACLMessage buyMessage = AgentUtil.createMessage(getAID(), buyOrder, ACLMessage.REQUEST, Ontology.ORDER);
                    ACLMessage sellMessage = AgentUtil.createMessage(getAID(), sellOrder, ACLMessage.REQUEST, Ontology.ORDER);
                    send(buyMessage);
                    send(sellMessage);
                    // pobranie akcji, na które zostało wystawione zlecenie sprzedaży
                    int currentQuantity = stocks.get(sellOrder.stock);
                    stocks.put(sellOrder.stock, currentQuantity - sellOrder.getQuantity());
                    readyToTrade = false;
                }
            }
        });

        //odbieranie zakupionych akcji
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(addStocksTemplate);
                if(message == null) {
                    block();
                }
                else {
                    StocksTransferRequest stocksTransferRequest = gson.fromJson(message.getContent(), StocksTransferRequest.class);
                    Integer currentAmount = stocks.get(stocksTransferRequest.stock);
                    if(currentAmount != null) {
                        stocks.put(stocksTransferRequest.stock, currentAmount + stocksTransferRequest.quantity);
                    }
                    else {
                        stocks.put(stocksTransferRequest.stock, stocksTransferRequest.quantity);
                    }
                }
            }
        });

        //CheckFunds
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage queryMessage = AgentUtil.createMessage(getAID(), new CheckFundsRequest(this.getAgent().getAID().getName(), 0,0),  ACLMessage.REQUEST, Ontology.FUNDS_REQUEST, bankAID);
                send(queryMessage);
                addBehaviour(new Behaviour() {
                    boolean done = false;
                    @Override
                    public void action() {
                        ACLMessage response = receive(checkFundsTemplate);
                        if(response == null) block();
                        else {
                            CheckFundsRequest fundsResponse = gson.fromJson(response.getContent(), CheckFundsRequest.class);
                            funds = fundsResponse.funds;
                            clear = fundsResponse.clear;
                            done = true;
                        }
                    }
                    @Override
                    public boolean done() {
                        return done;
                    }

                });
            }
        });
    }


}
