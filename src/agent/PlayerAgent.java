package agent;

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
        bankAID = new AID("bank-0", AID.ISLOCALNAME);
        brokerAID = new AID("broker-0", AID.ISLOCALNAME);
        strategy = Strategy.fromString((String) getArguments()[0]);
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage queryMessage = createMessage(new PortfolioRequest(), ACLMessage.REQUEST, brokerAID);
                queryMessage.setOntology(Ontology.PORTFOLIO_REQUEST);
            }
        });
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage queryMessage = createMessage(new ShowFundsRequest(0), ACLMessage.REQUEST, bankAID);
                queryMessage.setOntology(Ontology.FUNDS_REQUEST);
                send(queryMessage);
            }
        });
    }

    protected <T> ACLMessage createMessage(T messageObject, int intent, AID... sellerAIDs) {
        ACLMessage message = new ACLMessage(intent);
        message.setSender(this.getAID());
        Arrays.asList(sellerAIDs).forEach(message::addReceiver);
        message.setContent(gson.toJson(messageObject));
        return message;
    }

    protected <T> T receiveMessageResponse(Class<T> c) {
        ACLMessage message = receive();
        return message == null? null: gson.fromJson(message.getContent(), c);
    }
}
