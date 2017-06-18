import com.google.gson.Gson;
import model.MarketInfo;
import resource.data.FileShareCreator;
import resource.data.ShareCreator;


import java.io.IOException;
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

        ShareCreator shareCreator;
        final String SHARE_PATH = "E:\\Workspace\\GIT\\SAG\\SAGAgentStockSimulator\\src\\shares.properties";
        try {
            shareCreator = new FileShareCreator(SHARE_PATH);
            shareCreator.initializeShares();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
