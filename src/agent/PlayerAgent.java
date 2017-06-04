package agent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import model.Stock;

public class PlayerAgent extends Agent {

    @Override
    protected void setup() {
        super.setup();
    }

    protected ACLMessage createOfferMessage(Stock stock, int amount, int unitPrice) {
        ACLMessage aclMessage = new ACLMessage(ACLMessage.PROPOSE);
        aclMessage.setSender(this.getAID());
        aclMessage.setContent(String.format("{stock: %s, amount: %d, unitPrice: %d}", stock, amount, unitPrice));
        return aclMessage;
    }
}
