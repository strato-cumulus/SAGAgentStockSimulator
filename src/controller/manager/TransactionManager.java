package controller.manager;

import agent.BankAgent;
import agent.util.AgentUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;
import model.Ontology;
import model.order.BuyOrder;
import model.order.SellOrder;
import model.request.BlockFundsRequest;
import model.request.CommitTransactionRequest;
import model.transaction.Transaction;

import java.util.List;

/**
 * Created by Marcin on 06.06.2017.
 */
public class TransactionManager extends CyclicBehaviour {

    private Agent agent;
    private List<SellOrder> sellOrders;
    private List<BuyOrder> buyOrders;
    private List<Transaction> transactions;
    private MessageTemplate evalTemplate = MessageTemplate.MatchOntology(Ontology.EVALUATE);

    public TransactionManager (Agent agent, List<SellOrder> sellOrders, List<BuyOrder> buyOrders) {
        this.agent = agent;
        this.sellOrders = sellOrders;
        this.buyOrders = buyOrders;
    }

    @Override
    public void action() {
        ACLMessage message = agent.receive(evalTemplate);
        if (message == null) {
            block();
        } else {
            buyOrderLoop:
            for(BuyOrder buyOrder: buyOrders) {
                for(SellOrder sellOrder: sellOrders) {
                    if(buyOrder.getStock().equals(sellOrder.getStock()) && buyOrder.getUnitPrice() >= sellOrder.getUnitPrice()) {
                        int transactionQuantity = buyOrder.getQuantity() > sellOrder.getQuantity() ? buyOrder.getQuantity() : sellOrder.getQuantity();
                        int transactionUnitPrice = sellOrder.getUnitPrice();
                        transactions.add(new Transaction(buyOrder.getPlayerAID(),
                                                         sellOrder.getPlayerAID(),
                                                         buyOrder.getStock(),
                                                         transactionQuantity,
                                                         transactionUnitPrice
                                                         ));
                        buyOrder.setQuantity(buyOrder.getQuantity()-transactionQuantity);
                        sellOrder.setQuantity(sellOrder.getQuantity()-transactionQuantity);
                        if(buyOrder.getQuantity()==0) buyOrders.remove(buyOrder);
                        if(sellOrder.getQuantity()==0) sellOrders.remove(sellOrder);
                        CommitTransactionRequest commitTransactionRequest = new CommitTransactionRequest(buyOrder.getPlayerAID().getName(), transactionUnitPrice*transactionQuantity);
                        agent.send(AgentUtil.createMessage(agent.getAID(), commitTransactionRequest, ACLMessage.REQUEST, Ontology.COMMIT_TRANSACTION, BankAgent.aid));
                        //TODO calculate price
                        break buyOrderLoop;
                    }
                }
            }
        }
    }
}
