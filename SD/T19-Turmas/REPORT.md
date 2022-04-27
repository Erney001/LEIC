# Report

### Solution explanation

Each unit that can be changed, Student and Class, has an attribute
*version* to mark the date when the last update to it was done.

When resolving conflicts, during the state propagation, we use
the registered timestamps of each unit and compare them.

The most recent changes prevail over the old ones in the same
unit, unless they collide with the info from the Class (eg.: a
Student cannot enroll in a Secondary server after the
enrollments were closed in the Primary server).

The oldest enrollments prevail over the new ones, no matter
in what server they happen.

<br>

#### Notes

- The solution handles 3 or more servers

- We didn't modificate the Admin commands to work with 2 or
  more Secondary servers, so their effect will be applied on 1
  of the possible servers

---

### Test 1

Goal: Test when 2 students try to enroll at the "same time"
in 2 different servers in a class with a capacity of 1 student only.

Result: The student that enrolls first gets the place.

Commands:
```
@Admin      deactivateGossip P
@Admin      deactivateGossip S
@Professor  openEnrollments 1
@Admin      gossip P
@Admin      dump S
@Admin      deactivate S
@Student1   enroll
@Admin      deactivate P
@Admin      activate S
@Student2   enroll
@Admin      activate P
@Admin      dump P
@Admin      dump S
@Admin      gossip P
@Admin      dump S
@Admin      gossip S
@Admin      dump P
@Admin      dump S
```

### Test 2

Goal : Test when a student is trying to enroll in one secondary
server and the enrollments are already closed in the primary
server.

Result: The student will be discarded afterwards.

Commands:
```
@Admin      deactivateGossip P
@Admin      deactivateGossip S
@Professor  openEnrollments 1
@Admin      gossip P
@Admin      dump S
@Professor  closeEnrollments
@Admin      deactivate P
@Student    enroll
@Admin      activate P
@Admin      dump P
@Admin      dump S
@Admin      gossip P
@Admin      dump S
@Admin      gossip S
@Admin      dump P
@Admin      dump S
```
---
