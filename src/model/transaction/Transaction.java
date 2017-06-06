package model.transaction;

import jade.core.AID;
import model.Stock;

public class Transaction {
    private AID buyerAID;
    private AID sellerAID;
    private Stock stock;
    private int transactionQuantity;
    private int transactionPrice;

    public Transaction(AID buyerAID, AID sellerAID, Stock stock, int transactionQuantity, int transactionPrice) {
        this.buyerAID = buyerAID;
        this.sellerAID = sellerAID;
        this.stock = stock;
        this.transactionQuantity = transactionQuantity;
        this.transactionPrice = transactionPrice;

    }

    public AID getBuyerAID() {
        return buyerAID;
    }

    public void setBuyerAID(AID buyerAID) {
        this.buyerAID = buyerAID;
    }

    public AID getSellerAID() {
        return sellerAID;
    }

    public void setSellerAID(AID sellerAID) {
        this.sellerAID = sellerAID;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
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
