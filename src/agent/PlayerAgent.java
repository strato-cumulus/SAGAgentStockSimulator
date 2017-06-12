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
import model.Portfolio;
import model.request.AddAccountRequest;
import model.request.PortfolioRequest;
import model.request.SellOrdersRequest;
import model.request.ShowFundsRequest;
import strategy.Strategy;

public class PlayerAgent extends Agent {

    private Gson gson = new Gson();
    private AID bankAID;
    private AID brokerAID;
    private Strategy strategy;
    private MessageTemplate sellOrdersRequestTemplate = MessageTemplate.MatchOntology(Ontology.SELL_ORDERS_REQUEST);

    @Override
    protected void setup() {
        super.setup();
        bankAID = BankAgent.aid;
        brokerAID = BrokerAgent.aid;
        strategy = Strategy.fromString((String) getArguments()[0]);
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                AddAccountRequest accountRequest = new AddAccountRequest(this.getAgent().getAID().getName(), Integer.parseInt((String) getArguments()[1]));
                send(AgentUtil.createMessage(getAID(), accountRequest,  ACLMessage.REQUEST, Ontology.ADD_ACCOUNT, bankAID));
            }
        });
        addBehaviour(new TickerBehaviour(this, 100) {
            @Override
            public void onTick() {
                ACLMessage queryMessage = AgentUtil.createMessage(getAID(), new SellOrdersRequest(),  ACLMessage.REQUEST, Ontology.SELL_ORDERS_REQUEST, brokerAID);
                send(queryMessage);
                addBehaviour(new Behaviour() {
                    boolean done = false;
                    @Override
                    public void action() {
                        ACLMessage response = receive(sellOrdersRequestTemplate);
                        if(response == null) block();
                        else {
                            PortfolioRequest portfolioRequest = gson.fromJson(portfolioResponse.getContent(), PortfolioRequest.class);
                            //TODO deserialize
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
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage queryMessage = AgentUtil.createMessage(getAID(), new ShowFundsRequest(0), ACLMessage.REQUEST, Ontology.FUNDS_REQUEST, bankAID);
                send(queryMessage);
            }
        });
    }


}
