package model.order;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Created by Marcin on 13.06.2017.
 */
public class Order {
    public final OrderType type;
    public final String stock;
    private int quantity;
    public final int unitPrice;
    public final String playerName;
    private LocalDateTime arrivalTime;

    public Order(OrderType type, String stock, int quantity, int unitPrice, String playerName) {
        this.type = type;
        this.stock = stock;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.playerName = playerName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public static Comparator<Order> SellOrderComparator = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            Integer price1 = o1.unitPrice;
            Integer price2 = o2.unitPrice;
            if(price1 == price2) {
                LocalDateTime time1 = o1.getArrivalTime();
                LocalDateTime time2 = o2.getArrivalTime();
                return time1.compareTo(time2);
            }
            else if (price1 > price2) {
                return 1;
            }
            else {
                return -1;
            }
        }
    };

    public static Comparator<Order> BuyOrderComparator = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            Integer price1 = o1.unitPrice;
            Integer price2 = o2.unitPrice;
            if(price1 == price2) {
                LocalDateTime time1 = o1.getArrivalTime();
                LocalDateTime time2 = o2.getArrivalTime();
                return time1.compareTo(time2);
            }
            else if (price1 > price2) {
                return -1;
            }
            else {
                return 1;
            }
        }
    };
}
