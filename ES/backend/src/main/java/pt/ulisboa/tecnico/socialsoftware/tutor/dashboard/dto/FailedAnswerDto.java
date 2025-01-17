package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.FailedAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

public class FailedAnswerDto implements Serializable {
   
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("answered")
    private boolean answered;
    
    @JsonProperty("collected")
    private String collected;
    
    @JsonProperty("questionContent")
    private String questionContent;
    
    @JsonProperty("questionId")
    private Integer questionId;
    
    private QuestionAnswerDto questionAnswerDto;
    
    public FailedAnswerDto(){
    }
    
    public FailedAnswerDto(FailedAnswer failedAnswer){
        setId(failedAnswer.getId());
        setAnswered(failedAnswer.getAnswered());
        setCollected(DateHandler.toISOString(failedAnswer.getCollected()));
        setQuestionAnswerDto(new QuestionAnswerDto(failedAnswer.getQuestionAnswer()));
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public boolean getAnswered() {
        return answered;
    }
    
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
    
    public String getCollected() {
        return collected;
    }
    
    public void setCollected(String collected) {
        this.collected = collected;
    }
    
    public QuestionAnswerDto getQuestionAnswerDto() {
        return questionAnswerDto;
    }
    
    public void setQuestionAnswerDto(QuestionAnswerDto questionAnswerDto) {
        this.questionAnswerDto = questionAnswerDto;
        this.questionContent = questionAnswerDto.getQuestion().getContent();
        this.questionId= questionAnswerDto.getQuestion().getId();

    }
    @Override
    public String toString() {
        return "FailedAnswerDto{" +
                "id=" + id +
                ", answered=" + answered +
                "}";
    }
}