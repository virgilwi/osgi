package com.example.client.api;

import java.util.List;

public interface ClientService {
    void addClient(Client client);
    List<Client> getClients();
    void updateClients(List<Client> clients);
}
