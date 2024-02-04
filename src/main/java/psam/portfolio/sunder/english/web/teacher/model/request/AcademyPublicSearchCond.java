package psam.portfolio.sunder.english.web.teacher.model.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

@Getter
public class AcademyPublicSearchCond extends SearchCond {

    private String academyName;

    @AssertTrue
    private Boolean openToPublic;

    @Builder
    public AcademyPublicSearchCond(Integer page, Integer size, String prop, String order, String academyName) {
        super(page, size, prop, order);
        this.openToPublic = true;
        this.academyName = academyName;
    }
}
