package model.request;

import jade.core.AID;

public class AddAccountRequest {

    public final String agentName;
    public final int initialFunds;

    public AddAccountRequest(String agentName, int initialFunds) {
        this.agentName = agentName;
        this.initialFunds = initialFunds;
    }
}
