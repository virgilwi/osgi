package com.example.client.impl;

import com.example.client.api.Client;
import com.example.client.api.ClientService;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientServiceImpl implements ClientService {
    private final List<Client> clients = new ArrayList<>();

    @Override
    public void addClient(Client client) {
        clients.add(client);
    }

    @Override
    public List<Client> getClients() {
        return new ArrayList<>(clients);
    }

    @Override
    public void updateClients(List<Client> updatedClients) {
        for (Client updatedClient : updatedClients) {
            for (Client client : clients) {
                if (client.getId().equals(updatedClient.getId())) {
                    client.setSorted(updatedClient.isSorted());
                }
            }
        }
    }
}
