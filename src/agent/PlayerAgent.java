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
import model.order.BuyOrder;
import model.order.Order;
import model.order.SellOrder;
import model.request.AddAccountRequest;
import model.request.EquilibriumRequest;
import model.request.ShowFundsRequest;
import model.request.TradeRequest;
import strategy.Strategy;

import java.util.List;
import java.util.Map;

public class PlayerAgent extends Agent {

    private Gson gson = new Gson();
    private AID bankAID;
    private AID brokerAID;
    private Strategy strategy;
    private boolean readyToTrade;
    private SellOrder sellOrder;
    private BuyOrder buyOrder;
    private MessageTemplate equilibriumTemplate = MessageTemplate.MatchOntology(Ontology.EQUILIBRIUM_REQUEST);

    @Override
    protected void setup() {
        super.setup();
        bankAID = BankAgent.aid;
        brokerAID = BrokerAgent.aid;
        strategy = Strategy.fromString((String) getArguments()[0]);
        readyToTrade = false;

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                AddAccountRequest accountRequest = new AddAccountRequest(this.getAgent().getAID().getName(), Integer.parseInt((String) getArguments()[1]));
                send(AgentUtil.createMessage(getAID(), accountRequest,  ACLMessage.REQUEST, Ontology.ADD_ACCOUNT, bankAID));
            }
        });

        //EquilibriumRequest
        addBehaviour(new TickerBehaviour(this, 100) {
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
                            System.out.println(equilibriumResponse.equilibriumPrice );
                            //calculate best offer
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
                    ACLMessage buyMessage = AgentUtil.createMessage(getAID(), new TradeRequest(buyOrder), ACLMessage.REQUEST, Ontology.BUY_ORDER);
                    ACLMessage sellMessage = AgentUtil.createMessage(getAID(), new TradeRequest(sellOrder), ACLMessage.REQUEST, Ontology.SELL_ORDER);
                    send(buyMessage);
                    send(sellMessage);
                }
            }
        });

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage queryMessage = AgentUtil.createMessage(getAID(), new ShowFundsRequest(0), ACLMessage.REQUEST, Ontology.FUNDS_REQUEST, bankAID);
                send(queryMessage);
            }
        });
    }


}
