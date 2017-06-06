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
import model.Stock;
import model.account.Account;
import model.order.BuyOrder;
import model.order.SellOrder;
import model.request.BlockFundsRequest;
import model.request.PortfolioRequest;
import resource.ResourceCreationException;
import resource.data.FileShareCreator;
import resource.data.ShareCreator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BrokerAgent extends Agent {

    public static final AID aid = new AID("broker-0", AID.ISLOCALNAME);

    private ShareCreator shareCreator;
    private int gameDuration;
    private Portfolio portfolio;
    private Map<AID, Account> players;

    private MessageTemplate portfolioTemplate = MessageTemplate.MatchOntology("portfolio");
    private MessageTemplate evaluate = MessageTemplate.MatchOntology("evaluate");
    private MessageTemplate buyTemplate = MessageTemplate.MatchOntology("buy");
    private MessageTemplate sellTemplate = MessageTemplate.MatchOntology("sell");
    private MessageTemplate blockTemplate = MessageTemplate.MatchOntology(Ontology.BLOCK_FUNDS);

    private List<SellOrder> sellOrders = new LinkedList<>();
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

        //TODO nie działa
        //initializeShares();

        // add Transaction Manager
        addBehaviour(new TransactionManager(this, sellOrders, buyOrders));

        //Portfolio
        addBehaviour(new TickerBehaviour(this, 10) {
            private PortfolioRequest portfolioRequest;
            private AID senderAID;
            @Override
            public void onTick() {
                ACLMessage message = receive(portfolioTemplate);
                if(message == null) {
                    block();
                }
                else {
                    portfolioRequest = gson.fromJson(message.getContent(), PortfolioRequest.class);
                    senderAID = message.getSender();
                    portfolioRequest.portfolio.copy(portfolio);
                    send(AgentUtil.createMessage(getAID(), portfolioRequest, ACLMessage.REQUEST, Ontology.PORTFOLIO_REQUEST, senderAID));
                    players.putIfAbsent(senderAID, new Account());
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
    }

    protected void initializeShares() {
        try {
            shareCreator = new FileShareCreator("src/shares.properties");
            portfolio = shareCreator.createShares();
        } catch (ResourceCreationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        (new BrokerAgent()).initializeShares();
    }
}


