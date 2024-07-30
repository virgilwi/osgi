package com.example.client.camel;

import com.example.client.api.Client;
import com.example.client.api.ClientService;
import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.stream.Collectors;

@Component(immediate = true)
public class ClientRoute extends RouteBuilder {
    @Reference
    private ClientService clientService;

    @Override
    public void configure() throws Exception {
        from("timer:foo?period=60000")
                .process(exchange -> {
                    List<Client> clients = clientService.getClients();

                    // Фильтрация клиентов для уведомления
                    List<Client> clientsToNotify = clients.stream()
                            .filter(client -> client.getEmail().contains("example.com"))
                            .collect(Collectors.toList());

                    // Остальные клиенты
                    List<Client> otherClients = clients.stream()
                            .filter(client -> !client.getEmail().contains("example.com"))
                            .collect(Collectors.toList());

                    // Обновление клиентов
                    clientService.updateClients(otherClients);

                    // Отправка уведомлений
                    for (Client client : clientsToNotify) {
                        exchange.getContext().createProducerTemplate()
                                .sendBody("direct:notify", client);
                    }
                });

        from("direct:notify")
                .log("Sending notification to client: ${body}");
    }
}
