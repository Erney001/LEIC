package pt.tecnico.grpc.server;

import pt.tecnico.grpc.Banking.RegisterResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Bank {
    private static final Logger LOGGER = Logger.getLogger(Bank.class.getName());
    ConcurrentHashMap<String, Integer> clients = new ConcurrentHashMap<>();

    public void register(String client, Integer balance) {
        clients.put(client, balance);
        LOGGER.info("User: " + client + " has been instantiated with balance: " + balance);
    }

    public int getBalance(String name) {
        return clients.get(name);
    }

    public boolean isClient(String name) {
        return clients.containsKey(name);
    }

    public void zero(String client){
        clients.put(client, 0);
    }

    public void transfer(String from, String to, int amount){
        clients.put(from, clients.get(from) - amount);
        clients.put(to,  clients.get(to) + amount);
    }
}
