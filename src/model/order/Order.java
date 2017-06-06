package model.order;

import jade.core.AID;
import model.Share;

public abstract class Order {

    protected final Share share;
    private int quantity;
    private final String playerName;

    public Order(Share share, int quantity, String playerName) {
        this.share = share;
        this.quantity = quantity;
        this.playerName = playerName;
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

    public String getPlayerName() {
        return this.playerName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
