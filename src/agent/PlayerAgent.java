package agent;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public class PlayerAgent extends Agent {

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new Behaviour() {
            @Override
            public void action() {

            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }
}
