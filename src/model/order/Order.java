package model.order;

import jade.core.AID;
import model.Stock;

public abstract class Order {

    protected final Stock stock;
    private int quantity;
    private int unitPrice;
    private final AID playerAID;

    public Order(Stock stock, int quantity, AID playerAID) {
        this.stock = stock;
        this.quantity = quantity;
        this.playerAID = playerAID;
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public AID getPlayerAID() {
        return this.playerAID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getTotalPrice() {
        return unitPrice * quantity;
    }
}
