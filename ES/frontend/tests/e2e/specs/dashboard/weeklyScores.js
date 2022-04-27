describe('Student Walkthrough', () => {
    beforeEach(() => {
        cy.deleteQuestionsAndAnswers();
        cy.deleteWeeklyScores();
        //create quiz
        cy.demoTeacherLogin();
        cy.createQuestion(
            'Question Title',
            'Question',
            'Option',
            'Option',
            'Option',
            'Correct'
        );
        cy.createQuestion(
            'Question Title2',
            'Question',
            'Option',
            'Option',
            'Option',
            'Correct'
        );
        cy.createQuizzWith2Questions(
            'Quiz Title',
            'Question Title',
            'Question Title2'
        );
        cy.contains('Logout').click();
    });

    afterEach(() => {
        cy.deleteFailedAnswers();
        cy.deleteQuestionsAndAnswers();
        cy.deleteWeeklyScores();
    });

    it('student gets weekly scores', () => {
        cy.intercept('GET', '**/students/dashboards/executions/*').as('getDashboard');
        cy.intercept('GET', '**/students/dashboards/*/weeklyScores').as('getWeeklyScores');
        cy.intercept('PUT', '**/students/dashboards/*/weeklyScores').as('updateWeeklyScores');
        cy.intercept('DELETE', '**/students/dashboards/*/weeklyScores/*').as('deleteWeeklyScore');

        cy.demoStudentLogin();

        cy.createWeeklyScore();

        cy.solveQuizz('Quiz Title', 2);

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getDashboard');

        cy.get('[data-cy="weeklyScoresMenuButton"]').click();
        cy.wait('@getWeeklyScores');

        cy.get('[data-cy="weeklyScoresRefreshButton"]').click()
        cy.wait('@updateWeeklyScores')

        cy.get('[data-cy="weeklyScoresDeleteButton"]').should('have.length', 2).eq(1).click();
        cy.wait('@deleteWeeklyScore');

        cy.get('[data-cy="weeklyScoresDeleteButton"]').should('have.length', 1).eq(0).click();
        cy.wait('@deleteWeeklyScore');

        cy.closeErrorMessage();

        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });
    });
});
