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
public class StudyWordPATCHCorrect {

    @NotNull
    private Long id;
    @NotNull
    private Boolean correct;
    private String reason;
}
