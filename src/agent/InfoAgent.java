package agent;

import com.google.gson.Gson;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import model.Ontology;
import model.messagecontent.Information;
import resource.ResourceCreationException;
import resource.data.FileShareCreator;
import resource.data.ShareCreator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class InfoAgent extends Agent {

    private Gson gson = new Gson();
    private Random random = new Random();
    private List<String> tickerCodes;

    @Override
    protected void setup() {
        super.setup();

        try {
            ShareCreator shareCreator = new FileShareCreator(BrokerAgent.SHARE_PATH);
            tickerCodes = new ArrayList<>();
            tickerCodes.addAll(shareCreator.getAllStocks());
        }
        catch(ResourceCreationException e) {
            tickerCodes = Collections.emptyList();
        }

        int tick = (int) ((random.nextGaussian() + 1) * 2000);

        addBehaviour(new TickerBehaviour(this, tick) {
            @Override
            protected void onTick() {
                if(random.nextGaussian() > -.5) {
                    ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                    message.setOntology(Ontology.INFORMATION);
                    message.setContent(gson.toJson(new Information(
                            tickerCodes.get(Math.abs(random.nextInt() % tickerCodes.size())),
                            random.nextInt() % 5,
                            LocalDateTime.now())));
                    message.addReceiver(BrokerAgent.aid);
                    send(message);
                }
            }
        });
    }
}
