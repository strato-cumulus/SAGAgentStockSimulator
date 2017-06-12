package model.request;

public class AddAccountRequest {

    public final String agentName;
    public final int initialFunds;

    public AddAccountRequest(String agentName, int initialFunds) {
        this.agentName = agentName;
        this.initialFunds = initialFunds;
    }
}
