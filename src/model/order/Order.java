package model.order;

public abstract class Order {

    protected final String stock;
    private int quantity;
    private int unitPrice;
    private final String playerName;

    public Order(String stock, int quantity, String playerName) {
        this.stock = stock;
        this.quantity = quantity;
        this.playerName = playerName;
    }

    public String getStock() {
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
