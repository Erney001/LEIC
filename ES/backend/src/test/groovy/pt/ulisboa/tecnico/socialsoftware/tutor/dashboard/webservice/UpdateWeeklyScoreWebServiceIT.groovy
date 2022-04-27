package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateWeeklyScoreWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def response

    def authUserDto
    def courseExecutionDto
    def dashboardDto

    def setup() {
        given:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        courseExecutionDto = courseService.getDemoCourse()
        authUserDto = authUserService.demoStudentAuth(false).getUser()
        dashboardDto = dashboardService.getDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())
    }

    def "demo student updates its weekly scores"() {
        given: 'demo student'
        demoStudentLogin()

        dashboardDto.getLastCheckWeeklyScores() != null
        
        when: 'the web service is invoked'
        response = restClient.put(
            path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyScores',
            requestContentType: 'application/json'
        )

        then: "the request returns 200"
        response.status == 200
        dashboardService.getDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId()).getLastCheckWeeklyScores() != null
    }

    def "demo teacher does not have access"() {
        given: 'demo teacher'
        demoTeacherLogin()
        
        when: 'the web service is invoked'
        response = restClient.put(
            path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyScores',
            requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        dashboardService.getDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId()).getLastCheckWeeklyScores() == null
    }

    def "student cant update another students weekly score"() {
        given: 'new student'
        def student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        createExternalCourseAndExecution()
        student.getAuthUser().setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when: 'the web service is invoked'
        response = restClient.put(
            path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyScores',
            requestContentType: 'application/json'
        )

        then: "the server understands the request but refuses to authorize it"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        dashboardService.getDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId()).getLastCheckWeeklyScores() == null
    }

    def cleanup() {
        userRepository.deleteAll()
        courseRepository.deleteAll()
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }
}