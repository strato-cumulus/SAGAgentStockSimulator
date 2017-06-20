package agent;

import agent.util.AgentUtil;
import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.account.Account;
import model.request.AddAccountRequest;
import model.request.BlockFundsRequest;
import model.request.CheckFundsRequest;
import model.request.MoneyTransferRequest;

import java.util.HashMap;
import java.util.Map;

public class BankAgent extends Agent {

    public static final int DEFAULT_CYCLE_WAIT = 500;
    public static final AID aid = new AID("bank-0", AID.ISLOCALNAME);
    private Gson gson = new Gson();
    private AID brokerAID;
    private final MessageTemplate addAccountTemplate = MessageTemplate.MatchOntology(Ontology.ADD_ACCOUNT);
    private final MessageTemplate checkFundsTemplate = MessageTemplate.MatchOntology(Ontology.CHECK_FUNDS);
    private final MessageTemplate blockFundsTemplate = MessageTemplate.and(MessageTemplate.MatchSender(BrokerAgent.aid), MessageTemplate.MatchOntology(Ontology.BLOCK_FUNDS));
    private final MessageTemplate transferTemplate = MessageTemplate.and(MessageTemplate.MatchSender(BrokerAgent.aid), MessageTemplate.MatchOntology(Ontology.TRANSFER));
    private final MessageTemplate terminateTemplate = MessageTemplate.MatchOntology(Ontology.TERMINATE);

    private Map<String, Account> accounts = new HashMap<>();

    @Override
    public void setup() {
        super.setup();
        brokerAID = BrokerAgent.aid;

        // rejestracja agenta u brokera
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                send(AgentUtil.createMessage(getAID(), "", ACLMessage.REQUEST, Ontology.REGISTER_REQUEST, brokerAID));
            }
        });

        // Add account
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(addAccountTemplate);
                if(message == null) block();
                else {
                    AddAccountRequest request = gson.fromJson(message.getContent(), AddAccountRequest.class);
                    accounts.putIfAbsent(request.agentName, new Account(request.initialFunds));
                    System.out.println("BANK/DODANO KONTO DLA: \t\t" + request.agentName + ", STAN: " + request.initialFunds);
                }
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

        // Money transfer
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(transferTemplate);
                if(message == null) block();
                else {
                    MoneyTransferRequest request = gson.fromJson(message.getContent(), MoneyTransferRequest.class);
                    Account accountFrom = accounts.get(request.playerFrom);
                    Account accountTo = accounts.get(request.playerTo);
                    accountFrom.unblockFunds(request.blockedAmount);
                    accountFrom.blockFunds(request.amount);
                    accountFrom.subtract(request.amount);
                    accountTo.add(request.amount);
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        //oczekiwanie na zakończenie symulacji
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(terminateTemplate);
                if (message == null) {
                    block();
                } else {
                    doDelete();
                }
            }
        });
    }
}
