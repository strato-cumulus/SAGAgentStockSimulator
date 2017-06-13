package strategy;

import com.google.gson.Gson;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Ontology;
import model.order.Order;
import model.request.EquilibriumRequest;

public abstract class Strategy {

    protected static final double maxPartSpent = .1;
    private MessageTemplate strategyMessageTemplate = MessageTemplate.MatchOntology(Ontology.EQUILIBRIUM_REQUEST);
    private Gson gson = new Gson();

    public static Strategy fromString(String strategyName) {
        switch(strategyName) {
            case "InformationResponding": return new InformationResponding();
            case "OnFallBuying": return new OnFallBuying();
            case "OnRiseBuying": return new OnRiseBuying();
            case "OnFallSelling": return new OnFallSelling();
        }
        return null;
    }

    public MessageTemplate getMessageTemplate() {
        return strategyMessageTemplate;
    }

    public ACLMessage createDataRequest() {
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setOntology(Ontology.EQUILIBRIUM_REQUEST);
        message.setContent(gson.toJson(new EquilibriumRequest()));
        return message;
    }

    public abstract Order perform(ACLMessage message, int funds);

    protected EquilibriumRequest unpack(ACLMessage message) {
        return gson.fromJson(message.getContent(), EquilibriumRequest.class);
    }
}