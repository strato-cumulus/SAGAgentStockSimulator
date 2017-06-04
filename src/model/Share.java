package model;

public final class Share {

    private final Stock stock;
    private final int price;

    public Share(Stock stock, int price) {
        this.stock = stock;
        this.price = price;
    }

    public Stock getStock() {
        return this.stock;
    }

    public int getPrice() {
        return price;
    }
}
