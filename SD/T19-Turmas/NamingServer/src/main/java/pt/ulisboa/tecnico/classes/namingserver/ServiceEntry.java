package pt.ulisboa.tecnico.classes.namingserver;

import java.util.ArrayList;
import java.util.List;

public class ServiceEntry {
    private final String _serviceName;
    private final List<ServerEntry> _serviceProviders;

    public ServiceEntry(String serviceName, List<ServerEntry> serviceProviders) {
        _serviceName = serviceName;
        _serviceProviders = new ArrayList<>(serviceProviders);
    }

    String getServiceName() {
        return _serviceName;
    }

    List<ServerEntry> getServiceProviders() {
        return _serviceProviders;
    }

    void addServiceProvider(ServerEntry provider) {
        _serviceProviders.add(provider);
    }

    void removeServiceProvider(String target) {
        _serviceProviders.removeIf(serverEntry -> serverEntry.getTarget().equals(target));
    }

    List<ServerEntry> getQualifiedServiceProviders(List<String> qualifiers) {
        return _serviceProviders.stream().filter(serverEntry -> serverEntry.hasAtLeastOneQualifier(qualifiers)).toList();
    }
}
