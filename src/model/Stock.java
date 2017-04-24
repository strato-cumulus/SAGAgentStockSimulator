package model;

public class Stock {

    private final String tickerCode;

    public Stock(String tickerCode) {
        this.tickerCode = tickerCode;
    }

    public String getTickerCode() {
        return tickerCode;
    }
}