package psam.portfolio.sunder.english.domain.study.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyWordFullResponse {

    private Long id;
    private String question;
    private String submit;
    private String answer;
    private Boolean correct;
    private String reason;
    private List<String> choices;

    public static StudyWordFullResponse from(StudyWord studyWord, boolean canSeeAnswer) {
        return StudyWordFullResponse.builder()
                .id(studyWord.getId())
                .question(studyWord.getQuestion())
                .submit(studyWord.getSubmit())
                .answer(canSeeAnswer ? studyWord.getAnswer() : null)
                .correct(studyWord.getCorrect())
                .reason(studyWord.getReason())
                .choices(studyWord.getChoices())
                .build();
    }
}
