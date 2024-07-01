package psam.portfolio.sunder.english.domain.study.model.request;

import lombok.Getter;
import psam.portfolio.sunder.english.global.slicing.SlicingSearchCond;

import java.time.LocalDateTime;

@Getter
public class StudySlicingSearchCond extends SlicingSearchCond {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String bookKeyword;
    private String studentName;
    private Integer schoolGrade;

    public StudySlicingSearchCond(Integer size, Long lastSequence, LocalDateTime startDateTime, LocalDateTime endDateTime, String bookKeyword, String studentName, Integer schoolGrade) {
        super(size, lastSequence);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.bookKeyword = bookKeyword;
        this.studentName = studentName;
        this.schoolGrade = schoolGrade;
    }
}
