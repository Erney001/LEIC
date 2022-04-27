package pt.ulisboa.tecnico.classes.namingserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NamingServices {
    private final Map<String, ServiceEntry> _services;
    private static boolean _debug;

    public NamingServices(boolean debug) {
        _services = new ConcurrentHashMap<>();
        _debug = debug;
        debug("NamingServices class created with debug flag activated");
    }

    synchronized void addServiceEntry(String serviceName, ServerEntry serverEntry) {
        String target = serverEntry.getTarget();
        if (_services.containsKey(serviceName)) {
            _services.get(serviceName).addServiceProvider(serverEntry);
            debug("Added new service provider " + target + " to already existing service " + serviceName);
        } else {
            _services.put(serviceName, new ServiceEntry(serviceName, List.of(serverEntry)));
            debug("Added new service provider " + target + " to new service " + serviceName);
        }
    }

    synchronized void removeServiceEntry(String serviceName, String target) {
        if (_services.containsKey(serviceName)) {
            _services.get(serviceName).removeServiceProvider(target);
            debug("Removed service provider " + target + " associated with service " + serviceName);
        } else {
            debug("Cannot remove service provider " + target + " from non-existent service " + serviceName);
        }
    }

    synchronized List<String> getServiceProviders(String serviceName, List<String> qualifiers) {
        if (_services.containsKey(serviceName)) {
            debug("The list of service providers for the service " + serviceName + " was returned");
            return _services.get(serviceName)
                    .getQualifiedServiceProviders(qualifiers)
                    .stream()
                    .map(ServerEntry::getTarget)
                    .toList();
        }
        debug("Returned an empty list of service providers for non-existent service: " + serviceName);
        return new ArrayList<>();
    }

    private static void debug(String debugMessage) {
        if (_debug) {
            System.err.println("[DEBUG] " + debugMessage);
        }
    }
}
