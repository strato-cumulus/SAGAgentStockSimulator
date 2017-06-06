package model.transaction;

import jade.core.AID;
import model.Share;

/**
 * Created by Marcin on 06.06.2017.
 */
public class Transaction {
    private String buyerName;
    private String sellerName;
    private Share transactionShare;
    private int transactionQuantity;
    private int transactionPrice;

    public Transaction(String buyerAID, String sellerName, Share transactionShare, int transactionQuantity, int transactionPrice) {
        this.buyerName = buyerName;
        this.sellerName = sellerName;
        this.transactionShare = transactionShare;
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

    public Share getTransactionShare() {
        return transactionShare;
    }

    public void setTransactionShare(Share transactionShare) {
        this.transactionShare = transactionShare;
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
