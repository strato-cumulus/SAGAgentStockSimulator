package model.request;

public class CheckFundsRequest {

    public final String accountName;
    public final int funds;
    public final int clear;

    public CheckFundsRequest(String accountName, int funds, int clear) {
        this.accountName = accountName;
        this.funds = funds;
        this.clear = clear;
    }

    public CheckFundsRequest(String accountName) {
        this(accountName, 0, 0);
    }
}
