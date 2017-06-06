package agent;

import agent.util.AgentUtil;
import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.account.Account;
import model.request.AddAccountRequest;
import model.request.BlockFundsRequest;
import model.request.CheckFundsRequest;
import model.request.CommitTransactionRequest;

import java.util.HashMap;
import java.util.Map;

public class BankAgent extends Agent {

    public static final AID aid = new AID("bank-0", AID.ISLOCALNAME);
    private Gson gson = new Gson();

    private final MessageTemplate addAccountTemplate = MessageTemplate.MatchOntology(Ontology.ADD_ACCOUNT);
    private final MessageTemplate checkFundsTemplate = MessageTemplate.MatchOntology(Ontology.CHECK_FUNDS);
    private final MessageTemplate blockFundsTemplate = MessageTemplate.and(MessageTemplate.MatchSender(BrokerAgent.aid), MessageTemplate.MatchOntology(Ontology.BLOCK_FUNDS));
    private final MessageTemplate commitTransactionTemplate = MessageTemplate.and(MessageTemplate.MatchSender(BrokerAgent.aid), MessageTemplate.MatchOntology(Ontology.COMMIT_TRANSACTION));

    private Map<String, Account> accounts = new HashMap<>();

    @Override
    public void setup() {

        // Add account
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(addAccountTemplate);
                if(message == null) block();
                else {
                    AddAccountRequest request = gson.fromJson(message.getContent(), AddAccountRequest.class);
                    accounts.putIfAbsent(request.agentName, new Account(request.initialFunds));
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        // Check funds
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(checkFundsTemplate);
                if (message == null) block();
                else {
                    CheckFundsRequest query = gson.fromJson(message.getContent(), CheckFundsRequest.class);
                    Account account = accounts.get(query.accountName);
                    CheckFundsRequest reply = new CheckFundsRequest(query.accountName, account.getFunds(), account.getClear());
                    send(AgentUtil.createMessage(getAID(), reply, ACLMessage.INFORM, Ontology.CHECK_FUNDS, BrokerAgent.aid));
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        // Block or unblock funds
        addBehaviour(new Behaviour() {

            @Override
            public void action() {
                ACLMessage message = receive(blockFundsTemplate);
                if(message == null) block();
                else {
                    BlockFundsRequest query = gson.fromJson(message.getContent(), BlockFundsRequest.class);
                    Account account = accounts.get(query.agentName);
                    boolean blockResult = true;
                    if(query.blockAmount > 0) {
                        blockResult = account.blockFunds(query.blockAmount);
                    }
                    else {
                        account.unblockFunds(query.blockAmount);
                    }
                    BlockFundsRequest reply = new BlockFundsRequest(aid.getName(), query.blockAmount, blockResult);
                    send(AgentUtil.createMessage(getAID(), reply, ACLMessage.INFORM, Ontology.BLOCK_FUNDS, BrokerAgent.aid));
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        // Add or subtract funds (commit tx)
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(commitTransactionTemplate);
                if(message == null) block();
                else {
                    CommitTransactionRequest request = gson.fromJson(message.getContent(), CommitTransactionRequest.class);
                    Account account = accounts.get(request.agentName);
                    account.subtract(request.amount);
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }
}
