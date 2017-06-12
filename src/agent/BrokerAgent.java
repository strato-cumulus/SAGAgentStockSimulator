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
import model.order.BuyOrder;
import model.order.SellOrder;
import model.request.BlockFundsRequest;
import model.request.EquilibriumRequest;
import model.request.PortfolioRequest;
import resource.ResourceCreationException;
import resource.data.FileShareCreator;
import resource.data.ShareCreator;

import java.util.LinkedList;
import java.util.List;

public class BrokerAgent extends Agent {

    public static final AID aid = new AID("broker-0", AID.ISLOCALNAME);

    private ShareCreator shareCreator;
    private int gameDuration;
    private Portfolio portfolio;
    private EquilibriumRequest equilibrium = new EquilibriumRequest();

    private MessageTemplate equilibriumTemplate = MessageTemplate.MatchOntology(Ontology.EQUILIBRIUM_REQUEST);
    private MessageTemplate portfolioTemplate = MessageTemplate.MatchOntology(Ontology.PORTFOLIO_REQUEST);
    private MessageTemplate buyTemplate = MessageTemplate.MatchOntology(Ontology.BUY_ORDER);
    private MessageTemplate sellTemplate = MessageTemplate.MatchOntology(Ontology.SELL_TRANSACTION);
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

        initializeShares();

        // add Transaction Manager
        addBehaviour(new TransactionManager(this, sellOrders, buyOrders, equilibrium));

        //EquilibriumRequest
        addBehaviour(new TickerBehaviour(this, 10) {
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
            shareCreator = new FileShareCreator("C:\\Users\\filip\\Documents\\GitHub\\SAGAgentStockSimulator\\src\\shares.properties");
            portfolio = shareCreator.createShares();
            equilibrium.equilibriumPrice.putAll(portfolio.getInitialEquilibriumPrice());
        } catch (ResourceCreationException e) {
            e.printStackTrace();
        }
    }

}


