package com.example.client.impl;

import com.example.client.api.ClientService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
    private ServiceRegistration<ClientService> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        ClientService clientService = new ClientServiceImpl();
        registration = context.registerService(ClientService.class, clientService, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
