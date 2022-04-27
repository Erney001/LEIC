package pt.ulisboa.tecnico.classes.classserver;

import java.util.Date;

public class StudentState {
    private final String _studentId;
    private final String _studentName;
    private int _version;

    StudentState(String studentId, String studentName){
        _studentId = studentId;
        _studentName = studentName;
        _version = (int) new Date().getTime();
    }

    public String getId() {
        return _studentId;
    }

    public String getName() {
        return _studentName;
    }

    public int getVersion() {
        return _version;
    }

    public void updateVersion(){
        _version = (int) new Date().getTime();
    }

    public void defineNewVersion(int new_version){
        _version = new_version;
    }

}
