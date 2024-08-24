package psam.portfolio.sunder.english.domain.academy.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.pagination.PageSearchCond;

@Getter
public class AcademyPublicPageSearchCond extends PageSearchCond {

    private final String academyName;
    private final String academyAddress;

    @Builder
    public AcademyPublicPageSearchCond(Integer page, Integer size, String prop, String dir, String academyName, String academyAddress) {
        super(page, size, prop, dir);
        this.academyName = academyName;
        this.academyAddress = academyAddress;
    }
}
