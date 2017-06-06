package agent;

import agent.util.AgentUtil;
import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import model.Ontology;
import model.request.PortfolioRequest;
import model.request.ShowFundsRequest;
import strategy.Strategy;

import java.util.Arrays;

public class PlayerAgent extends Agent {

    private Gson gson = new Gson();
    private AID bankAID;
    private AID brokerAID;
    private Strategy strategy;

    @Override
    protected void setup() {
        super.setup();
        bankAID = BankAgent.aid;
        brokerAID = BrokerAgent.aid;
        strategy = Strategy.fromString((String) getArguments()[0]);
        System.out.println("Player");
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage queryMessage = AgentUtil.createMessage(getAID(), new PortfolioRequest(),  ACLMessage.REQUEST, Ontology.PORTFOLIO_REQUEST, brokerAID);
               // queryMessage.setOntology(Ontology.PORTFOLIO_REQUEST);
            }
        });
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage queryMessage = AgentUtil.createMessage(getAID(), new ShowFundsRequest(0), ACLMessage.REQUEST, Ontology.FUNDS_REQUEST, bankAID);
                //queryMessage.setOntology(Ontology.FUNDS_REQUEST);
                send(queryMessage);
            }
        });
    }


}
