package model.request;

import model.Share;
import model.Stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortfolioRequest {

    public final Map<Stock, List<Share>> portfolio = new HashMap<>();
}
