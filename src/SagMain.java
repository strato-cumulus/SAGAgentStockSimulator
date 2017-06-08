import jade.Boot;
import model.math.Line;
import model.math.Point;

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



        Line LA = Line.getLineFromPoints(new Point(2.0, 2.0 ), new Point(-2.0, -2.0));
        Line LB = Line.getLineFromPoints(new Point(1.0, 0.0 ), new Point(0.0, 1.0));

        //Line LA = new Line (1.0, 0.0);
        //Line LB = new Line (-1.0, 0.0);

        Point inter = LA.getIntersection(LB);
        System.out.println("x: " + inter.getX());
        System.out.println("y: " + inter.getY());
    }
}
