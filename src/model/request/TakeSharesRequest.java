package model.request;

import model.Share;

import java.util.ArrayList;
import java.util.List;

public class TakeSharesRequest {

    public final int amount;
    public final List<Share> shares = new ArrayList<>();

    public TakeSharesRequest(int amount) {
        this.amount = amount;
    }
}
