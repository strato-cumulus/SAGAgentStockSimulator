package model.transaction;

public class Transaction {
    private String buyerName;
    private String sellerName;
    private String stock;
    private int transactionQuantity;
    private int transactionPrice;

    public Transaction(String buyerAID, String sellerName, String stock, int transactionQuantity, int transactionPrice) {
        this.buyerName = buyerName;
        this.sellerName = sellerName;
        this.stock = stock;
        this.transactionQuantity = transactionQuantity;
        this.transactionPrice = transactionPrice;

    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public int getTransactionQuantity() {
        return transactionQuantity;
    }

    public void setTransactionQuantity(int transactionQuantity) {
        this.transactionQuantity = transactionQuantity;
    }

    public int getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(int transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

}
