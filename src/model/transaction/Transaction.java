package model.transaction;

public class Transaction {
    public final String buyerName;
    public final String sellerName;
    public final String stock;
    public final int quantity;
    public final int price;
    public final int offerPrice;

    public Transaction(String buyerName, String sellerName, String stock, int quantity, int price, int offerPrice) {
        this.buyerName = buyerName;
        this.sellerName = sellerName;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.offerPrice = offerPrice;
    }
}
