package psam.portfolio.sunder.english.web.academy.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

@Getter
public class AcademyPublicSearchCond extends SearchCond {

    private final String academyName;

    @Builder
    public AcademyPublicSearchCond(Integer page, Integer size, String prop, String order, String academyName) {
        super(page, size, prop, order);
        this.academyName = academyName;
    }
}
