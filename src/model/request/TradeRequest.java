package model.request;

import model.order.Order;

/**
 * Created by marcin on 12.06.2017.
 */
public class TradeRequest {
    public Order order;

    public TradeRequest(Order order) {
        this.order = order;
    }
}
