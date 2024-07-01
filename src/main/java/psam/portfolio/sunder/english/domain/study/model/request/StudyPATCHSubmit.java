package psam.portfolio.sunder.english.domain.study.model.request;

import jakarta.validation.constraints.NotNull;
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
        @NotNull
        private Long id;
        private String submit;
    }
}
