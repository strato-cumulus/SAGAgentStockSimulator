package controller.manager;

import agent.BankAgent;
import agent.util.AgentUtil;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.math.Line;
import model.math.Point;
import model.order.BuyOrder;
import model.order.SellOrder;
import model.request.CommitTransactionRequest;
import model.request.EquilibriumRequest;
import model.transaction.Transaction;

import java.util.*;

public class TransactionManager extends CyclicBehaviour {


    private Agent agent;
    private List<SellOrder> sellOrders;
    private List<BuyOrder> buyOrders;
    private List<Transaction> transactions;
    private EquilibriumRequest equilibrium;
    private MessageTemplate evalTemplate = MessageTemplate.MatchOntology(Ontology.EVALUATE);

    public TransactionManager (Agent agent, List<SellOrder> sellOrders, List<BuyOrder> buyOrders, EquilibriumRequest equilibrium) {
        this.agent = agent;
        this.sellOrders = sellOrders;
        this.buyOrders = buyOrders;
        this.equilibrium = equilibrium;
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
                        transactions.add(new Transaction(buyOrder.getPlayerName(), sellOrder.getPlayerName(), buyOrder.getStock(), transactionQuantity, transactionUnitPrice ));
                        buyOrder.setQuantity(buyOrder.getQuantity()-transactionQuantity);
                        sellOrder.setQuantity(sellOrder.getQuantity()-transactionQuantity);
                        if(buyOrder.getQuantity()==0) buyOrders.remove(buyOrder);
                        if(sellOrder.getQuantity()==0) sellOrders.remove(sellOrder);
                        CommitTransactionRequest commitTransactionRequest = new CommitTransactionRequest(buyOrder.getPlayerName(), transactionUnitPrice*transactionQuantity);
                        agent.send(AgentUtil.createMessage(agent.getAID(), commitTransactionRequest, ACLMessage.REQUEST, Ontology.COMMIT_TRANSACTION, BankAgent.aid));
                        break buyOrderLoop;
                    }
                }
            }
        }
    }
}
