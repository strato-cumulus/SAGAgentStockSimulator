package model.request;

import model.Stock;

public class TakeSharesRequest {

    public final Stock stock;
    public final int amount;

    public TakeSharesRequest(Stock stock, int amount) {
        this.stock = stock;
        this.amount = amount;
    }
}
