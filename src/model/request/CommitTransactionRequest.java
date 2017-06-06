package model.request;

import jade.core.AID;

public class CommitTransactionRequest {

    public final String agentName;
    public final int amount;

    public CommitTransactionRequest(String agentName, int amount) {
        this.agentName = agentName;
        this.amount = amount;
    }
}
