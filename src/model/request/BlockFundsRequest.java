package model.request;

import jade.core.AID;

public class BlockFundsRequest {

    public final AID aid;
    public final int blockAmount;
    public final boolean result;

    public BlockFundsRequest(AID aid, int blockAmount, boolean result) {
        this.aid = aid;
        this.blockAmount = blockAmount;
        this.result = result;
    }

    public BlockFundsRequest(AID aid, int blockAmount) {
        this(aid, blockAmount, false);
    }
}
