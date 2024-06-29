package psam.portfolio.sunder.english.domain.study.model.request;

import lombok.Getter;
import psam.portfolio.sunder.english.global.slicing.SlicingSearchCond;

@Getter
public class StudySlicingSearchCond extends SlicingSearchCond {

    public StudySlicingSearchCond(Integer size, Long lastSequence) {
        super(size, lastSequence);
    }
}
