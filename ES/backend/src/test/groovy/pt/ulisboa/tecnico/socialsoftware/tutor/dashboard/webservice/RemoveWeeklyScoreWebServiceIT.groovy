package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.WeeklyScore
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveWeeklyScoreWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def student
    def dashboard

    def response
    def weeklyScore

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.getAuthUser().setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        dashboard = new Dashboard(externalCourseExecution, student)
        dashboardRepository.save(dashboard)
        and:
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate week = DateHandler.now().minusDays(30).with(weekSunday).toLocalDate();
        weeklyScore = new WeeklyScore(dashboard, week)
        weeklyScoreRepository.save(weeklyScore)
    }

    def "student removes weekly score"() {
        given: "login by student"
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when: "the web service is invoked"
        response = restClient.delete(
                path: '/students/dashboards/' + dashboard.getId() +'/weeklyScores/' + weeklyScore.getId(),
                requestContentType: 'application/json'
        )

        then: "check response status"
        response.status == 200
        and: "the weeklyScore was removed from the database"
        weeklyScoreRepository.findById(weeklyScore.getId()).isEmpty()
        weeklyScoreRepository.count() == 0L
    }

    def "teacher can't remove student's weekly score from dashboard"() {
        given: "a teacher login"
        def teacher = new Teacher(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        teacher.getAuthUser().setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)
        createdUserLogin(USER_2_USERNAME, USER_2_PASSWORD)

        when: "the web service is invoked"
        response = restClient.delete(
                path: '/students/dashboards/' + dashboard.getId() +'/weeklyScores/' + weeklyScore.getId(),
                requestContentType: 'application/json'
        )

        then: "the server refuses to authorize it because user has not a student role"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and: "database has the weeklyScore"
        weeklyScoreRepository.findById(weeklyScore.getId()).isPresent()
        weeklyScoreRepository.count() == 1L
    }

    def "new student can't remove student's weekly score from dashboard"() {
        given: "a new student login"
        def newStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.EXTERNAL)
        newStudent.getAuthUser().setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        newStudent.addCourse(externalCourseExecution)
        userRepository.save(newStudent)
        createdUserLogin(USER_2_USERNAME, USER_2_PASSWORD)

        when: "the web service is invoked"
        response = restClient.delete(
                path: '/students/dashboards/' + dashboard.getId() +'/weeklyScores/' + weeklyScore.getId(),
                requestContentType: 'application/json'
        )

        then: "the server refuses to authorize it because the dashboard doesn't belong to the user"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and: "database has the weeklyScore"
        weeklyScoreRepository.findById(weeklyScore.getId()).isPresent()
        weeklyScoreRepository.count() == 1L
    }

    def cleanup() {
        userRepository.deleteAll()
        courseRepository.deleteAll()
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }

}
