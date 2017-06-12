package model.request;

public class TakeSharesRequest {

    public final String stock;
    public final int amount;

    public TakeSharesRequest(String stock, int amount) {
        this.stock = stock;
        this.amount = amount;
    }
}
