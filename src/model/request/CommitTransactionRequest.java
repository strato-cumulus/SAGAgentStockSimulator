package model.request;

import jade.core.AID;

public class CommitTransactionRequest {

    public final AID aid;
    public final int amount;

    public CommitTransactionRequest(AID aid, int amount) {
        this.aid = aid;
        this.amount = amount;
    }
}
