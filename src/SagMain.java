import com.google.gson.Gson;


import java.util.*;

public class SagMain {
    public static void main(String[] args){
//        String[] param = new String[ 5 ];
//        param[ 0 ] = "-gui";
//        param[ 1 ] = "-port";
//        param[ 2 ] = "12345";
//        param[ 3 ] = "-agents";
//        param[ 4 ] = "test:agent.BrokerAgent(1)";
//
//        Boot.main( param );

//
        Gson gson = new Gson();
//        SellOrder sell = new SellOrder("xx", 1, "seller", 3 );
//        BuyOrder buy = new BuyOrder("yy", 1, "buyer", 5);
//        String sellString = gson.toJson(sell);
//        String buyOrder = gson.toJson(buy);
//
////        Order order1 = gson.fromJson(sellString, Order.class);
//
//        List<List<Order>> list = new ArrayList<>();
//        List<Order> orders1 = new ArrayList<>();
//        List<Order> orders2 = new ArrayList<>();
//        Map<String, List<Order>> map = new HashMap<>();
//        orders1.add(new Order(OrderType.BUY, "xx", 1, 2, "xx"));
//        orders1.add(new Order(OrderType.BUY, "xx", 1, 2, "xx"));
//        orders2.add(new Order(OrderType.BUY, "xx", 1, 2, "xx"));
//        orders2.add(new Order(OrderType.BUY, "xx", 1, 2, "xx"));
//        list.add(orders1);
//        list.add(orders2);
//        map.put("xx", orders1);
//        map.put("yy", orders2);
//
//
//        Type type = new  TypeToken<Map<String, List<Order>>>(){}.getType();
//        Map<String, List<Order>> map2 = gson.fromJson(gson.toJson(map), type);
//        System.out.println(gson.toJson(map2));

        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        System.out.println(gson.toJson(list));
        Iterator<Integer> iterator = list.iterator();
        System.out.println(gson.toJson(list));
    }
}
