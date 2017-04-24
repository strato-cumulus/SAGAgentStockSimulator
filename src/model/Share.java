package model;

public final class Share {

    private final Stock stock;

    public Share(Stock stock) {
        this.stock = stock;
    }

    public Stock getStock() {
        return this.stock;
    }
}
