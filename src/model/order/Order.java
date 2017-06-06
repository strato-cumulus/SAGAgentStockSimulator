package model.order;

import jade.core.AID;
import model.Share;

public abstract class Order {

    protected final Share share;
    private int quantity;
    private final AID playerAID;

    public Order(Share share, int quantity, AID playerAID) {
        this.share = share;
        this.quantity = quantity;
        this.playerAID = playerAID;
    }

    public Share getShare() {
        return this.share;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getTotalPrice() {
        return this.quantity * this.share.getPrice();
    }

    public AID getPlayerAID() {
        return this.playerAID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
