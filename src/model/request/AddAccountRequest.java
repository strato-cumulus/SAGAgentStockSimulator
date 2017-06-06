package model.request;

import jade.core.AID;

public class AddAccountRequest {

    public final AID aid;
    public final int initialFunds;

    public AddAccountRequest(AID aid, int initialFunds) {
        this.aid = aid;
        this.initialFunds = initialFunds;
    }
}
