package resource.data;

import model.Share;
import model.Stock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ShareCreator {

    public abstract Map<Stock, Set<Share>> createShares();
}
