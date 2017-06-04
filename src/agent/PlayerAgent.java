package agent;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import model.order.BuyOrder;
import model.order.SellOrder;
import model.request.PortfolioRequest;
import model.request.TakeSharesRequest;
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
                sendPortfolioRequest(brokerAID);
                PortfolioRequest portfolioRequest = receivePortfolioRequestResponse();
                if(portfolioRequest == null) {
                    block();
                }

            }
        });
    }

    protected <T> void sendMessage(T messageObject, int intent, AID... sellerAIDs) {
        ACLMessage message = new ACLMessage(intent);
        message.setSender(this.getAID());
        Arrays.asList(sellerAIDs).forEach(message::addReceiver);
        message.setContent(gson.toJson(messageObject));
        this.send(message);
    }

    protected <T> T receiveMessageResponse(Class<T> c) {
        ACLMessage message = receive();
        return message == null? null: gson.fromJson(message.getContent(), c);
    }

    protected void sendBuyOrder(BuyOrder buyOrder, AID... sellerAIDs) {
        sendMessage(buyOrder, ACLMessage.PROPOSE, sellerAIDs);
    }

    protected BuyOrder receiveBuyOrderResponse() {
        return receiveMessageResponse(BuyOrder.class);
    }

    protected void sendSellOrder(SellOrder sellOrder, AID... buyerAIDs) {
        sendMessage(sellOrder, ACLMessage.PROPOSE, buyerAIDs);
    }

    protected SellOrder receiveSellOrderResponse() {
        return receiveMessageResponse(SellOrder.class);
    }

    protected void sendTakeSharesRequest(TakeSharesRequest request) {
        sendMessage(request, ACLMessage.PROPOSE, brokerAID);
    }

    protected TakeSharesRequest receiveTakeSharesRequestResponse() {
        return receiveMessageResponse(TakeSharesRequest.class);
    }

    protected void sendPortfolioRequest(AID... playerAIDs) {
        sendMessage(new PortfolioRequest(), ACLMessage.REQUEST, playerAIDs);
    }

    protected PortfolioRequest receivePortfolioRequestResponse() {
        return receiveMessageResponse(PortfolioRequest.class);
    }
}
