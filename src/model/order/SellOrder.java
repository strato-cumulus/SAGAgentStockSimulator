package model.order;

import model.Share;
import model.Stock;
import model.account.Account;

import java.util.Collection;
import java.util.Comparator;

public class SellOrder extends Order {

    public final long unitPrice;

    public SellOrder(Stock stock, long unitPrice, Collection<Share> shares) {
        super(stock, shares);
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