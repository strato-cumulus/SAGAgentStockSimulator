package controller;

import controller.manager.AccountManager;
import controller.manager.OrderManager;
import resource.data.FileShareCreator;
import resource.data.ShareCreator;

public class StockExchangeController {

    private final AccountManager accountManager;
    private final OrderManager orderManager;
    private final ShareCreator shareCreator;

    public StockExchangeController(ShareCreator shareCreator) {
        this.accountManager = new AccountManager();
        this.orderManager = new OrderManager();
        this.shareCreator = shareCreator;


    }
}
