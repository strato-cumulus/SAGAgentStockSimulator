package agent;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Share;
import model.Stock;
import model.account.Account;
import model.order.BuyOrder;
import model.order.SellOrder;
import model.request.PortfolioRequest;
import resource.ResourceCreationException;
import resource.data.FileShareCreator;
import resource.data.ShareCreator;
import java.util.*;

public class BrokerAgent extends Agent {
    private ShareCreator shareCreator;
    private int gameDuration;
    private Map<Stock, List<Share>> portfolio;
    private Map<AID, Account> players;

    private MessageTemplate portfolioTemplate = MessageTemplate.MatchOntology("portfolio");
    private MessageTemplate buyTemplate = MessageTemplate.MatchOntology("buy");
    private MessageTemplate sellTemplate = MessageTemplate.MatchOntology("sell");

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

        //Portfolio
        addBehaviour(new TickerBehaviour(this, 10) {
            private PortfolioRequest portfolioRequest;
            private AID senderAID;
            @Override
            public void onTick() {
                senderAID =
                        receiveMessage(PortfolioRequest.class, portfolioTemplate).keySet().stream().findFirst().get();
                portfolioRequest.portfolio.putAll(portfolio);
                sendMessage(portfolioRequest, "portfolio", senderAID);
                players.put(senderAID, new Account());
                if (getTickCount() > 100) {
                    stop();
                }
            }
        });

        //Buy order
        addBehaviour(new CyclicBehaviour() {
            private Map<AID, BuyOrder> messageContent = new HashMap<AID, BuyOrder>();
            private BuyOrder buyOrder;
            private AID senderAID;
            @Override
            public void action() {
                messageContent = receiveMessage(BuyOrder.class, buyTemplate);
                senderAID = messageContent.keySet().stream().findFirst().get();
                buyOrder = messageContent.get(senderAID);
                //TODO
            }
        });

        //Sell order
        addBehaviour(new CyclicBehaviour() {
            private Map<AID, SellOrder> messageContent = new HashMap<AID, SellOrder>();
            private SellOrder sellOrder;
            private AID senderAID;
            @Override
            public void action() {
                messageContent = receiveMessage(SellOrder.class, buyTemplate);
                senderAID = messageContent.keySet().stream().findFirst().get();
                sellOrder = messageContent.get(senderAID);
                //TODO
            }
        });
    }

    protected <T> void sendMessage(T messageObject, String ontology, AID... sellerAIDs) {
        ACLMessage message = new ACLMessage(0);
        message.setOntology(ontology);
        message.setSender(this.getAID());
        Arrays.asList(sellerAIDs).forEach(message::addReceiver);
        message.setContent(gson.toJson(messageObject));
        this.send(message);
    }

    protected <T> Map<AID, T>  receiveMessage(Class<T> c, MessageTemplate messageTemplate) {
        ACLMessage message = receive(messageTemplate);
        if (message != null) {
            Map<AID, T> returnValue = new HashMap<AID, T>();
            returnValue.put(message.getSender(), gson.fromJson(message.getContent(), c));
            return returnValue;
        }
        return null;
    }

    protected void initializeShares() {
        try {
            shareCreator = new FileShareCreator("shares.properties");
        } catch (ResourceCreationException e) {
            e.printStackTrace();
        }
        portfolio = shareCreator.createShares();
    }
}


