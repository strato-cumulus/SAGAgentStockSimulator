import jade.Boot;

public class SagMain {
    public static void main(String[] args){
        String[] param = new String[ 5 ];
        param[ 0 ] = "-gui";
        param[ 1 ] = "-port";
        param[ 2 ] = "12345";
        param[ 3 ] = "-agents";
        param[ 4 ] = "test:agent.BrokerAgent(1)";

        Boot.main( param );
    }
}
