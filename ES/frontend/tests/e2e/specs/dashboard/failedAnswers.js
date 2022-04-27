describe('Student Walkthrough', () => {
    let quizName;
    beforeEach(() => {
        cy.deleteQuestionsAndAnswers();

        //create quiz
        cy.demoTeacherLogin();
        let now_1 = Date.now();
        let event_1 = new Date(now_1);
        let question_1 = 'Question FailedAnswer 1 ' + event_1.toString()
        let questionContent_1 = 'Question FailedAnswer 1 ';
        cy.createQuestion(
            question_1,
            questionContent_1 ,
            'Option',
            'Option',
            'ChooseThisWrong',
            'Correct'
        );
        let now_2 = Date.now();
        let event_2 = new Date(now_2);
        let question_2 = 'Question FailedAnswer 2 ' + event_2.toString();
        let questionContent_2 = 'Question FailedAnswer 2 ';
        cy.createQuestion(
            question_2,
            questionContent_2 ,
            'Option',
            'Option',
            'ChooseThisWrong',
            'Correct'
        );
        let now_3 = Date.now();
        let event_3 = new Date(now_3);
        quizName = 'Failed Answer Title ' +  event_3.toString()
        cy.createQuizzWith2Questions(
            quizName,
            question_1,
            question_2
        );
        cy.contains('Logout').click();
    });

    afterEach(() => {
        cy.deleteFailedAnswers();
        cy.deleteQuestionsAndAnswers();
    });


    //All-in-One

    it('student accesses failed Answers', () => {
        cy.demoStudentLogin();
        cy.intercept('GET', '/students/dashboards/executions/*').as('getDashboard');
        cy.intercept('GET', '/students/dashboards/*/failedanswers').as('getFailedAnswers');
        cy.intercept('DELETE', '/students/failedanswers/*').as('deleteFailedAnswer');
        //Do Solve Quiz
        cy.get('[data-cy="quizzesStudentMenuButton"]').click();
        cy.contains('Available').click();

        cy.contains(quizName).click();

        let numberOfQuizQuestions = 2;
        for (let i = 1; i < numberOfQuizQuestions; i++) {
            //Click the wrong one
            cy.get('[data-cy="optionList"]').children().contains('ChooseThisWrong').click();
            cy.get('[data-cy="nextQuestionButton"]').click();
        }
        //Click the wrong one
        cy.get('[data-cy="optionList"]').children().contains('ChooseThisWrong').click();

        cy.get('[data-cy="endQuizButton"]').click();

        cy.get('[data-cy="confirmationButton"]').click();

        cy.get('[data-cy="nextQuestionButton"]').click();

        //Get dashboard
        cy.get('[data-cy="dashboardMenuButton"]').click();

        cy.wait('@getDashboard');

        cy.get('[data-cy="failedAnswersMenuButton"]').click();


        cy.get('[data-cy="failedAnswersRefreshButton"]').click();

        cy.wait('@getFailedAnswers');

        let shower;

        for (shower = 1 ; shower <= numberOfQuizQuestions ; shower++){

            cy.get('[data-cy="showStudentViewDialogButton"]').first(shower).click();
            //close
            cy.get('[data-cy="closeButton"]').click();
        }

        cy.get('[data-cy="failedAnswersDeleteButton"]').eq(0).click();
        cy.closeErrorMessage();

        cy.wait('@deleteFailedAnswer');

        cy.setFailedAnswersAsOld();


        cy.get('[data-cy="failedAnswersDeleteButton"]').eq(0).click();

        cy.wait('@deleteFailedAnswer');

        cy.wait(1000);

        cy.get('[data-cy="failedAnswersDeleteButton"]').click();

        cy.wait('@deleteFailedAnswer');

        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            console.log(err);
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });
    });

});