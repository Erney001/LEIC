package pt.ulisboa.tecnico.classes.classserver;

import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.ResponseCode;
import pt.ulisboa.tecnico.classes.contract.ClassesDefinitions.Student;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClassState {
    static final long PROPAGATION_DELAY = 15000;
    static final long PROPAGATION_PERIOD = 30000;

    private boolean _active;
    private int _capacity;
    private boolean _openEnrollments;
    private boolean _writingPermission;
    private boolean _gossip;
    private int _version;
    private boolean _debug;
    private Map<String, StudentState> _enrolled;
    private Map<String, StudentState> _discarded;
    private ClassServerFrontend _classServerFrontend;

    ClassState(boolean debug, boolean writingPermission, ClassServerFrontend classServerFrontend){
        _active = true;
        _capacity = 0;
        _openEnrollments = false;
        _writingPermission = writingPermission;
        _gossip = true;
        _version = (int) new Date().getTime();
        _debug = debug;
        _enrolled = new ConcurrentHashMap<>();
        _discarded = new ConcurrentHashMap<>();
        _classServerFrontend = classServerFrontend;
        debug("ClassState created");

        // Propagates state periodically
        class PropagateState extends TimerTask {
            @Override
            public void run() {
                if (_gossip) {
                    propagate();
                }
            }
        }

        Timer timer = new Timer();
        timer.schedule(new PropagateState(), PROPAGATION_DELAY, PROPAGATION_PERIOD);
    }

    synchronized int getCapacity() {
        return _capacity;
    }

    synchronized boolean areEnrollmentsOpen() {
        return _openEnrollments;
    }

    synchronized Map<String, StudentState> getEnrolled() {
        return _enrolled;
    }

    synchronized Map<String, StudentState> getDiscarded() {
        return _discarded;
    }

    // Enables the server class state for clients
    synchronized ResponseCode activate() {
        debug("activate: Server enabled");
        _active = true;
        return ResponseCode.OK;
    }

    // Disables the server class state for clients
    synchronized ResponseCode deactivate() {
        debug("deactivate: Server disabled");
        _active = false;
        return ResponseCode.OK;
    }

    synchronized ResponseCode activateGossip() {
        debug("activateGossip: gossip activated");
        _gossip = true;
        return ResponseCode.OK;
    }

    synchronized ResponseCode deactivateGossip() {
        debug("deactivateGossip: gossip deactivated");
        _gossip = false;
        return ResponseCode.OK;
    }

    synchronized ResponseCode gossip() {
        debug("gossip: gossip was forced");
        propagate();
        return ResponseCode.OK;
    }

    // Opens class enrollments for a limited capacity of students if they are closed.
    synchronized ResponseCode openEnrollments(int capacity) {
        ResponseCode code = checkServerState();
        if (code != ResponseCode.OK || !_writingPermission) {
            debug("openEnrollments: Server not OK with code=" + code);
            return code;
        }
        if (_openEnrollments) {
            debug("openEnrollments: Enrollments are already open");
            return ResponseCode.ENROLLMENTS_ALREADY_OPENED;
        }
        if (capacity <= _enrolled.size()) {
            debug("openEnrollments: Capacity less or equal to the number of students in class");
            return ResponseCode.FULL_CLASS;
        }

        _openEnrollments = true;
        _capacity = capacity;
        _version = (int) new Date().getTime();
        debug("openEnrollments: Enrollments opened with a capacity of " + capacity);
        debug("Version of change: " + _version);
        return ResponseCode.OK;
    }

    // Closes class enrollments if they are open.
    synchronized ResponseCode closeEnrollments() {
        ResponseCode code = checkServerState();
        if (code != ResponseCode.OK || !_writingPermission) {
            debug("closeEnrollments: Server not OK with code=" + code);
            return code;
        }
        if (!_openEnrollments) {
            debug("closeEnrollments: Enrollments are already closed");
            return ResponseCode.ENROLLMENTS_ALREADY_CLOSED;
        }

        _openEnrollments = false;
        _version = (int) new Date().getTime();
        debug("closeEnrollments: Enrollments are closed");
        debug("Version of change: " + _version);
        return ResponseCode.OK;
    }

    // Cancels the student's enrollment, if any.
    synchronized ResponseCode cancelEnrollment(String studentID, boolean force) {
        ResponseCode code = checkServerState();
        if (code != ResponseCode.OK || (!_writingPermission && !force)) {
            debug("cancelEnrollment: Server not OK with code=" + code);
            return code;
        }
        if (!_enrolled.containsKey(studentID)) {
            debug("cancelEnrollment: There is no student " + studentID + " in class");
            return ResponseCode.NON_EXISTING_STUDENT;
        }

        StudentState student = _enrolled.get(studentID);
        _enrolled.remove(studentID);
        _discarded.put(studentID, student);
        if (!force) {
            student.updateVersion();
        }
        debug("cancelEnrollment: Enrollment of the student " + studentID + " with name " +
                student.getName() + " canceled");
        debug("Student version of change: " + student.getVersion());
        debug("Version of change: " + _version);
        return ResponseCode.OK;
    }

    // Enrolls the student with given id and name, if not exist yet.
    synchronized ResponseCode enrollStudent(String studentId, String studentName, int version) {
        ResponseCode code = checkServerState();
        if (code != ResponseCode.OK) {
            debug("enrollStudent: Server not OK with code=" + code);
            return code;
        }
        if (!_openEnrollments && (version > _version)) {
            debug("enrollStudent: Enrollments are closed");
            return ResponseCode.ENROLLMENTS_ALREADY_CLOSED;
        }
        if (_enrolled.containsKey(studentId)) {
            debug("enrollStudent: Student is already in class");
            return ResponseCode.STUDENT_ALREADY_ENROLLED;
        }
        if (_enrolled.size() == _capacity) {
            debug("enrollStudent: Class is full");
            return ResponseCode.FULL_CLASS;
        }

        StudentState student = _discarded.get(studentId);
        if (student == null) {
            student = new StudentState(studentId, studentName);
        }
        _discarded.remove(studentId);
        _enrolled.put(studentId, student);
        student.defineNewVersion(version);
        debug("enrollStudent: Student " + studentId + " with name " + studentName + " is enrolled");
        debug("Student version of change: " + student.getVersion());
        debug("Version of change: " + _version);
        return ResponseCode.OK;
    }

    // Propagates server class state
    private synchronized void propagate() {
        if (_active) {
            List<ResponseCode> codes = _classServerFrontend.propagateState(this);
            if (codes == null) {
                debug("propagate: No servers detected");
            } else {
                for (ResponseCode code: codes) {
                    if (code == ResponseCode.OK) {
                        debug("propagate: State propagated successfully");
                    } else if (code == ResponseCode.INACTIVE_SERVER) {
                        debug("propagate: Server is inactive");
                    }
                }
            }
        } else {
            debug("propagate: Cannot execute propagate because server is not active");
        }
    }

    // Updates state from class state propagation
    synchronized ResponseCode propagateState(int version, int capacity, boolean openEnrollments,
                                                    List<Student> enrolled, List<Student> discarded) {
        if (!_active) {
            debug("propagateState: Server is disabled");
            return ResponseCode.INACTIVE_SERVER;
        }

        if (version > _version) {
            _capacity = capacity;
            _openEnrollments = openEnrollments;
            _version = version;

            if (!openEnrollments) {
                for (StudentState myStudent : _enrolled.values()) {
                    if (myStudent.getVersion() > version) {
                        cancelEnrollment(myStudent.getId(), true);
                    }
                }
            } else {
                for (StudentState myStudent : _discarded.values()) {
                    if (myStudent.getVersion() > version) {
                        enrollStudent(myStudent.getId(), myStudent.getName(), myStudent.getVersion());
                    }
                }
            }
        }

        for (Student student: discarded) {
            if (!_discarded.containsKey(student.getStudentId())) {
                if (_enrolled.containsKey(student.getStudentId())) {
                    StudentState studentState = _enrolled.get(student.getStudentId());
                    int studentStateVersion = studentState.getVersion();
                    if (student.getVersion() >= studentStateVersion) {
                        cancelEnrollment(student.getStudentId(), true);
                    }
                } else {
                    StudentState studentDiscard = new StudentState(student.getStudentId(), student.getStudentName());
                    studentDiscard.defineNewVersion(student.getVersion());
                    _discarded.put(student.getStudentId(), studentDiscard);
                }
            }
        }

        for (Student student: enrolled) {
            if (!_enrolled.containsKey(student.getStudentId())) {
                if (_discarded.containsKey(student.getStudentId())) {
                    StudentState studentState = _discarded.get(student.getStudentId());
                    int studentStateVersion = studentState.getVersion();
                    if (student.getVersion() >= studentStateVersion) {
                        if (_enrolled.size() == _capacity) {
                            for (StudentState myStudent : _enrolled.values()) {
                                if (myStudent.getVersion() > student.getVersion()) {
                                    cancelEnrollment(myStudent.getId(), true);
                                    enrollStudent(student.getStudentId(), student.getStudentName(), student.getVersion());
                                    break;
                                }
                            }
                        }
                        enrollStudent(student.getStudentId(), student.getStudentName(), student.getVersion());
                    }
                } else {
                    if (_enrolled.size() == _capacity) {
                        boolean discardAny = false;
                        for (StudentState myStudent: _enrolled.values()) {
                            if (myStudent.getVersion() > student.getVersion()) {
                                cancelEnrollment(myStudent.getId(), true);
                                enrollStudent(student.getStudentId(), student.getStudentName(), student.getVersion());
                                discardAny = true;
                                break;
                            }
                        }
                        if (!discardAny) {
                            StudentState studentDiscard = new StudentState(student.getStudentId(), student.getStudentName());
                            studentDiscard.defineNewVersion(student.getVersion());
                            _discarded.put(student.getStudentId(), studentDiscard);
                        }

                    } else {
                        enrollStudent(student.getStudentId(), student.getStudentName(), student.getVersion());
                        if (!_openEnrollments && student.getVersion() > _version) {
                            cancelEnrollment(student.getStudentId(), true);
                        }
                    }
                }
            }
        }

        return ResponseCode.OK;
    }

    synchronized ClassesDefinitions.ClassState toProto() {
        return ClassesDefinitions.ClassState.newBuilder()
                .setVersion(_version)
                .setCapacity(_capacity)
                .setOpenEnrollments(_openEnrollments)
                .addAllEnrolled(getEnrolled()
                    .keySet()
                    .stream()
                    .map(id -> ClassesDefinitions.Student.newBuilder()
                            .setVersion(getEnrolled().get(id).getVersion())
                            .setStudentId(id)
                            .setStudentName(getEnrolled().get(id).getName())
                            .build())
                    .toList())
                .addAllDiscarded(getDiscarded()
                    .keySet()
                    .stream()
                    .map(id -> ClassesDefinitions.Student.newBuilder()
                            .setVersion(getDiscarded().get(id).getVersion())
                            .setStudentId(id)
                            .setStudentName(getDiscarded().get(id).getName())
                            .build())
                    .toList())
                .build();
    }

    // Checks server's activity status
    synchronized ResponseCode checkServerState() {
        return _active ? ResponseCode.OK : ResponseCode.INACTIVE_SERVER;
    }

    private void debug(String debugMessage) {
        if (_debug) {
            System.err.println("[DEBUG] " + debugMessage);
        }
    }
}
