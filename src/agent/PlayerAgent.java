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
import model.request.AddAccountRequest;
import model.request.CheckFundsRequest;
import model.request.EquilibriumRequest;
import strategy.Strategy;

public class PlayerAgent extends Agent {

    private Gson gson = new Gson();
    private AID bankAID;
    private AID brokerAID;
    private Strategy buyStrategy;
    private Strategy sellStrategy;
    private boolean readyToTrade;
    private Order sellOrder;
    private Order buyOrder;
    private int funds;
    private int clear;
    private MessageTemplate equilibriumTemplate = MessageTemplate.MatchOntology(Ontology.EQUILIBRIUM_REQUEST);
    private final MessageTemplate checkFundsTemplate = MessageTemplate.MatchOntology(Ontology.CHECK_FUNDS);

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
                AddAccountRequest accountRequest = new AddAccountRequest(this.getAgent().getAID().getName(), Integer.parseInt((String) getArguments()[2]));
                send(AgentUtil.createMessage(getAID(), accountRequest,  ACLMessage.REQUEST, Ontology.ADD_ACCOUNT, bankAID));
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
                        ACLMessage response = receive(equilibriumTemplate);
                        if(response == null) block();
                        else {
                            EquilibriumRequest equilibriumResponse = gson.fromJson(response.getContent(), EquilibriumRequest.class);
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
                    ACLMessage buyMessage = AgentUtil.createMessage(getAID(), buyOrder, ACLMessage.REQUEST, Ontology.BUY_ORDER);
                    ACLMessage sellMessage = AgentUtil.createMessage(getAID(), sellOrder, ACLMessage.REQUEST, Ontology.SELL_ORDER);
                    send(buyMessage);
                    send(sellMessage);
                    readyToTrade = false;
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
