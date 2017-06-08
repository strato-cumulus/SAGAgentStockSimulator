package model.order;

import model.Stock;

public abstract class Order {

    protected final Stock stock;
    private int quantity;
    private int unitPrice;
    private final String playerName;

    public Order(Stock stock, int quantity, String playerName) {
        this.stock = stock;
        this.quantity = quantity;
        this.playerName = playerName;
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getPlayerName() {
        return this.playerName;
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
