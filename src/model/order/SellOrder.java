package model.order;

import jade.core.AID;
import model.Stock;

import java.util.Comparator;

public class SellOrder extends Order {

    public final long unitPrice;

    public SellOrder(Stock stock, int quantity, AID buyerAID, int unitPrice) {
        super(stock, quantity, buyerAID);
        this.unitPrice = unitPrice;
    }

    public static class ByPriceAscending implements Comparator<SellOrder> {

        @Override
        public int compare(SellOrder o1, SellOrder o2) {
            if(o1.unitPrice == o2.unitPrice) {
                return 0;
            }
            return o1.unitPrice < o2.unitPrice ? 1: -1;
        }
    }

    public static class ByPriceDescending implements Comparator<SellOrder> {

        @Override
        public int compare(SellOrder o1, SellOrder o2) {
            if(o1.unitPrice == o2.unitPrice) {
                return 0;
            }
            return o1.unitPrice > o2.unitPrice ? 1: -1;
        }
    }
}