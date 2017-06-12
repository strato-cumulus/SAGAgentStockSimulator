package model.request;

public class BlockFundsRequest {

    public final String agentName;
    public final int blockAmount;
    public final boolean result;

    public BlockFundsRequest(String agentName, int blockAmount, boolean result) {
        this.agentName = agentName;
        this.blockAmount = blockAmount;
        this.result = result;
    }

    public BlockFundsRequest(String agentName, int blockAmount) {
        this(agentName, blockAmount, false);
    }
}
