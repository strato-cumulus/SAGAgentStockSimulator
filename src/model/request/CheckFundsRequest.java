package model.request;

import jade.core.AID;

public class CheckFundsRequest {

    public final AID accountAID;
    public final int funds;
    public final int clear;

    public CheckFundsRequest(AID accountAID, int funds, int clear) {
        this.accountAID = accountAID;
        this.funds = funds;
        this.clear = clear;
    }

    public CheckFundsRequest(AID accountAID) {
        this(accountAID, 0, 0);
    }
}
