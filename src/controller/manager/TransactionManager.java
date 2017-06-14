package controller.manager;

import com.google.gson.Gson;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.order.*;
import model.request.EquilibriumRequest;
import model.transaction.Transaction;

import java.util.List;

public class TransactionManager extends CyclicBehaviour {
    private Agent agent;
    private List<Order> sellOrders;
    private List<Order> buyOrders;
    private List<Transaction> transactions;
    private EquilibriumRequest equilibrium;
    private MessageTemplate orderTemplate = MessageTemplate.MatchOntology(Ontology.ORDER);
    private Gson gson = new Gson();

    public TransactionManager (Agent agent, List<Order> sellOrders, List<Order> buyOrders, EquilibriumRequest equilibrium) {
        this.agent = agent;
        this.sellOrders = sellOrders;
        this.buyOrders = buyOrders;
        this.equilibrium = equilibrium;
    }

    @Override
    public void action() {
//        ACLMessage message = agent.receive(orderTemplate);
//        if (message == null) {
//            block();
//        } else {
//            Order order = gson.fromJson(message.getContent(), Order.class);
//            order.setArrivalTime(LocalDateTime.now());
//            if(order.type == OrderType.BUY) {
//                //doBuyTransaction
//
//            }
//            else {
//                //doSellTransaction
//            }
//        }
    }

//    private Transaction doBuyTransaction(Order buyOrder) {
//        int transactionAmount;
//        int buyer;
//        int seller;
//        int quantity;
//        int stock;
//        for (Order order : sellOrders) {
//            if(order.unitPrice <= buyOrder.unitPrice) {
//                if(order.getQuantity() == buyOrder.getQuantity()) {
//                    sellOrders.remove(order);
//
//                }
//                else if (order.getQuantity() > buyOrder.getQuantity()) {
//                    transactionAmount = buyOrder.getQuantity();
//
//                }
//                else {
//
//                }
//
//
//                Integer result = buyOrder.fullFillOrder(order.getQuantity());
//                if(result == 0) {
//
//                }
//            }
//        }
//    }

//    private Agent agent;
//    private List<SellOrder> sellOrders;
//    private List<BuyOrder> buyOrders;
//    private List<Transaction> transactions;
//    private EquilibriumRequest equilibrium;
//    private MessageTemplate evalTemplate = MessageTemplate.MatchOntology(Ontology.EVALUATE);
//    private Gson gson = new Gson();
//
//    public TransactionManager (Agent agent, List<SellOrder> sellOrders, List<BuyOrder> buyOrders, EquilibriumRequest equilibrium) {
//        this.agent = agent;
//        this.sellOrders = sellOrders;
//        this.buyOrders = buyOrders;
//        this.equilibrium = equilibrium;
//    }
//
//    @Override
//    public void action() {
//        ACLMessage message = agent.receive(evalTemplate);
//        if (message == null) {
//            block();
//        } else {
//            buyOrderLoop:
//            for(BuyOrder buyOrder: buyOrders) {
//                for(SellOrder sellOrder: sellOrders) {
//                    if(buyOrder.getStock().equals(sellOrder.getStock()) && buyOrder.getUnitPrice() >= sellOrder.getUnitPrice()) {
//                        int quantity = buyOrder.getQuantity() > sellOrder.getQuantity() ? buyOrder.getQuantity() : sellOrder.getQuantity();
//                        int transactionUnitPrice = sellOrder.getUnitPrice();
//                        transactions.add(new Transaction(buyOrder.getPlayerName(), sellOrder.getPlayerName(), buyOrder.getStock(), quantity, transactionUnitPrice ));
//                        buyOrder.setQuantity(buyOrder.getQuantity()-quantity);
//                        sellOrder.setQuantity(sellOrder.getQuantity()-quantity);
//                        if(buyOrder.getQuantity()==0) buyOrders.remove(buyOrder);
//                        if(sellOrder.getQuantity()==0) sellOrders.remove(sellOrder);
//                        MoneyTransferRequest commitTransactionRequest = new MoneyTransferRequest(buyOrder.getPlayerName(), sellOrder.getPlayerName(), transactionUnitPrice*quantity);
//                        agent.send(AgentUtil.createMessage(agent.getAID(), commitTransactionRequest, ACLMessage.REQUEST, Ontology.COMMIT_TRANSACTION, BankAgent.aid));
//                        break buyOrderLoop;
//                    }
//                }
//            }
//        }
//    }
}
