package resource.data;

import model.Portfolio;
import resource.ResourceCreationException;


public abstract class ShareCreator {

    public abstract Portfolio createShares() throws ResourceCreationException;
}