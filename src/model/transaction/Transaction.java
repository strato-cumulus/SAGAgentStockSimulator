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
}
