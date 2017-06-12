package model.request;

import model.Portfolio;
import model.Stock;
import model.order.SellOrder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SellOrdersRequest {
    public final List<SellOrder> sellOrders = new LinkedList<SellOrder>();
    public final Map<Stock, Integer> equilibriumPrice = new HashMap<Stock, Integer>();
}
