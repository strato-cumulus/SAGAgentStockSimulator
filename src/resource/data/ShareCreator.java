package resource.data;

import model.Share;
import model.Stock;
import resource.ResourceCreationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ShareCreator {

    public abstract Map<Stock, List<Share>> createShares() throws ResourceCreationException;
}
