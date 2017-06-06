package model.transaction;

import jade.core.AID;
import model.Share;

/**
 * Created by Marcin on 06.06.2017.
 */
public class Transaction {
    private AID buyerAID;
    private AID sellerAID;
    private Share transactionShare;
    private int transactionQuantity;
    private int transactionPrice;

    public Transaction(AID buyerAID, AID sellerAID, Share transactionShare, int transactionQuantity, int transactionPrice) {
        this.buyerAID = buyerAID;
        this.sellerAID = sellerAID;
        this.transactionShare = transactionShare;
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
