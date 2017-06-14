package model.request;

/**
 * Created by Marcin on 14.06.2017.
 */
public class StocksTransferRequest {
    public final String stock;
    public final int quantity;

    public StocksTransferRequest(String stock, int quantity) {
        this.stock = stock;
        this.quantity = quantity;
    }
}
