package psam.portfolio.sunder.english.domain.study.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPATCHSubmit {

    private List<StudyWordPATCHSubmit> studyWords;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudyWordPATCHSubmit {
        private Long id;
        private String submit;
    }
}
