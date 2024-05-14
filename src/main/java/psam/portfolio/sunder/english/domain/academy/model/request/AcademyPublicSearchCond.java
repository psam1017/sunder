package psam.portfolio.sunder.english.domain.academy.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

@Getter
public class AcademyPublicSearchCond extends SearchCond {

    private final String academyName;

    @Builder
    public AcademyPublicSearchCond(Integer page, Integer size, String prop, String dir, String academyName) {
        super(page, size, prop, dir);
        this.academyName = academyName;
    }
}
