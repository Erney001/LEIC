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
class GetWeeklyScoreWebServiceIT extends SpockTest {
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
        weeklyScoreService.updateWeeklyScore(dashboardDto.getId())
        and:
        createExternalCourseAndExecution()
    }

    def "demo student gets weekly scores"() {
        given:
        demoStudentLogin()

        when:
        response = restClient.get(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyScores',
                requestContentType: 'application/json'
        )

        then:
        response.status == 200
        and:
        def ws = weeklyScoreRepository.findAll().get(0)
        response.data.id == [ws.getId()]
        response.data.numberAnswered != null
        response.data.uniquelyAnswered != null
        response.data.percentageCorrect != null
        response.data.week != null
        and:
        weeklyScoreRepository.findAll().size() == 1
    }

    def "demo teacher does not have access"() {
        given:
        demoTeacherLogin()
        and:
        weeklyScoreRepository.findAll().size() == 0

        when:
        response = restClient.get(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyScores',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        weeklyScoreRepository.findAll().size() == 1
    }

    def "new demo student does not have access"() {
        given:
        def student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.getAuthUser().setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when:
        response = restClient.get(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyScores',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        weeklyScoreRepository.findAll().size() == 1
    }

    def cleanup(){
        userRepository.deleteAll()
        courseRepository.deleteAll()
        weeklyScoreRepository.deleteAll()
        dashboardRepository.deleteAll()
    }
}