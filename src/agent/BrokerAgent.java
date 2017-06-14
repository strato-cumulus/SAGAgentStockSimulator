package agent;

import agent.util.AgentUtil;
import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.Portfolio;
import model.MarketInfo;
import model.account.Account;
import model.order.Order;
import model.request.*;
import model.transaction.Transaction;
import resource.data.FileShareCreator;
import resource.data.ShareCreator;

import java.io.IOException;
import java.util.*;

public class BrokerAgent extends Agent {

    public static final String SHARE_PATH = "C:\\Users\\filip\\Documents\\GitHub\\SAGAgentStockSimulator\\src\\shares.properties";
    public static final AID aid = new AID("broker-0", AID.ISLOCALNAME);

    private MarketInfo marketInfo = new MarketInfo();
    private ShareCreator shareCreator;
    private int gameDuration;
    private Portfolio portfolio;
    private List<String> indexesList;
    private Map<AID, Account> players = new HashMap<AID, Account>();

    private MessageTemplate equilibriumReqTemplate = MessageTemplate.MatchOntology(Ontology.EQUILIBRIUM_REQUEST);
    private MessageTemplate infoTemplate = MessageTemplate.MatchOntology(Ontology.INFORMATION);
    private MessageTemplate orderTemplate = MessageTemplate.MatchOntology(Ontology.ORDER);
    private MessageTemplate transactionTemplate = MessageTemplate.MatchOntology(Ontology.TRANSACTION);
    private MessageTemplate equilibriumInfoTemplate = MessageTemplate.MatchOntology(Ontology.EQUILIBRIUM_INFO);

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

        //EquilibriumRequest - gracz pyta o informacje o giełdzie
        addBehaviour(new TickerBehaviour(this, 100) {
            private EquilibriumRequest equilibriumRequest = new EquilibriumRequest();
            private AID senderAID;
            @Override
            public void onTick() {
                ACLMessage message = receive(equilibriumReqTemplate);
                if(message == null) {
                    block();
                }
                else {
                    equilibriumRequest.updateHistPrices(marketInfo.getPricesHistory());
                    equilibriumRequest.updatePrices(marketInfo.getCurrPrices());
                    equilibriumRequest.updateInformation(marketInfo.getPositivities());
                    senderAID = message.getSender();
                    send(AgentUtil.createMessage(getAID(), equilibriumRequest, ACLMessage.REQUEST, Ontology.EQUILIBRIUM_RESPONSE, senderAID));
                    if (getTickCount() > 100) {
                        stop();
                    }
                }
            }
        });

        //odbieranie informacji o akcjach (wartość liczbowa generowana losowo co jakiś czas)
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(infoTemplate);
                if(message == null) {
                    block();
                }
                else {
                    InformationRequest request = gson.fromJson(message.getContent(), InformationRequest.class);
                    marketInfo.addPositivities(request.information);
                }
            }
        });

        //odbieranie kursu akcji od managerów transakcji
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(equilibriumInfoTemplate);
                if (message == null) {
                    block();
                } else {
                    int price = gson.fromJson(message.getContent(), Integer.class);
                    String stock = message.getSender().getLocalName();
                    marketInfo.addPrice(stock, price);
                }
            }
        });


        //odbieranie orderów od graczy i przekazywanie do odpowiednich managerów
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(orderTemplate);
                Order order = gson.fromJson(message.getContent(), Order.class);
                if (message == null) {
                    block();
                } else {
                    send(AgentUtil.createMessage(getAID(), message.getContent(), ACLMessage.REQUEST, Ontology.TRANSACTION, new AID(order.stock, AID.ISLOCALNAME)));
                }
            }
        });

        // odbieranie transakcji od managerów i ich finalizowanie
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(transactionTemplate);
                if (message == null) {
                    block();
                } else {
                    Transaction transaction = gson.fromJson(message.getContent(), Transaction.class);

                    MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest(
                            transaction.buyerName,
                            transaction.sellerName,
                            transaction.price * transaction.quantity,
                            transaction.offerPrice * transaction.quantity);

                    StocksTransferRequest stocksTransferRequest = new StocksTransferRequest(transaction.stock, transaction.quantity);
                    //obiciążenie konta gracza, który kupił (i odblokowanie zablokowanych środków) i zasilenie sprzedającego
                    send(AgentUtil.createMessage(getAID(), moneyTransferRequest, ACLMessage.REQUEST, Ontology.TRANSFER, BankAgent.aid));
                    //wysłanie akcji do playera, który kupił
                    send(AgentUtil.createMessage(getAID(), stocksTransferRequest, ACLMessage.REQUEST, Ontology.ADD_STOCKS, new AID(transaction.buyerName, AID.ISLOCALNAME)));

                }
            }
        });
    }

    private void initializeShares() {
        try {
            shareCreator = new FileShareCreator(SHARE_PATH);
            shareCreator.initializeShares();
            MarketInfo info = shareCreator.getInitialPrices();
            //TODO remove and fix strategies
            Map<String, Integer> price = new HashMap<>();
            price.putAll(marketInfo.getCurrPrices());
            marketInfo.updatePrices(price);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


