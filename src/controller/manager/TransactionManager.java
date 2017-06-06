package controller.manager;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.order.BuyOrder;
import model.order.SellOrder;
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

            //transaction evaluation

        }
    }
}
