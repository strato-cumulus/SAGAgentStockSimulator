package controller;

import model.Account;

import java.util.HashMap;
import java.util.Map;

public class AccountManager {

    private final Map<String, Account> accounts = new HashMap<>();

    public void registerAccount(String name) {
        accounts.put(name, new Account());
    }

    public void registerAccount(String name, long funds) {
        accounts.put(name, new Account(funds));
    }

    public Account getAccount(String name) {
        return accounts.get(name);
    }
}
