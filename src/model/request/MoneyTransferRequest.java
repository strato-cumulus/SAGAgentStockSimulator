package model.request;

public class MoneyTransferRequest {

    public final String playerFrom;
    public final String playerTo;
    public final int amount;
    public final int blockedAmount;

    public MoneyTransferRequest(String playerFrom, String playerTo, int amount, int blockedAmount) {
        this.playerFrom = playerFrom;
        this.playerTo = playerTo;
        this.amount = amount;
        this.blockedAmount = blockedAmount;
    }
}
