package pt.ulisboa.tecnico.classes.namingserver;

import java.util.List;

public class ServerEntry {
    private final String _target;
    private final List<String> _qualifiers;

    public ServerEntry(String target, List<String> qualifiers) {
        _target = target;
        _qualifiers = qualifiers;
    }

    String getTarget() {
        return _target;
    }

    List<String> getQualifiers() {
        return _qualifiers;
    }

    boolean hasAtLeastOneQualifier(List<String> qualifiers) {
        return _qualifiers.stream().anyMatch(qualifiers::contains);
    }

    boolean hasAllQualifiers(List<String> qualifiers) {
        return _qualifiers.containsAll(qualifiers);
    }
}
