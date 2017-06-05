package agent.util;

import com.google.gson.Gson;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;

public class AgentUtil {

    private static Gson gson = new Gson();

    public static <T> ACLMessage createMessage(AID senderAID, T messageObject, int intent, String ontology, AID... receiverAIDs) {
        ACLMessage message = new ACLMessage(intent);
        message.setSender(senderAID);
        message.setOntology(ontology);
        Arrays.asList(receiverAIDs).forEach(message::addReceiver);
        message.setContent(gson.toJson(messageObject));
        return message;
    }
}
