package psam.portfolio.sunder.english.domain.study.model.request;

import lombok.Getter;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.global.slicing.SlicingSearchCond;

import java.time.LocalDateTime;

@Getter
public class StudySlicingSearchCond extends SlicingSearchCond {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String studyTitle;
    private String studentName;
    private Integer schoolGrade;
    private StudyStatus studyStatus;

    public StudySlicingSearchCond(Integer size, Long lastSequence, LocalDateTime startDateTime, LocalDateTime endDateTime, String studyTitle, String studentName, Integer schoolGrade, StudyStatus studyStatus) {
        super(size, lastSequence);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        studyTitle = substring20AndToLowerCase(studyTitle);
        this.studyTitle = removeTwoWhiteSpaces(studyTitle);
        this.studentName = studentName;
        this.schoolGrade = schoolGrade;
        this.studyStatus = studyStatus;
    }

    private String substring20AndToLowerCase(String str) {
        if (StringUtils.hasText(str) && str.length() > 20) {
            return str.substring(0, 20).toLowerCase();
        }
        return str;
    }

    private String removeTwoWhiteSpaces(String str) {
        if (StringUtils.hasText(str)) {
            while (str.contains("  ")) {
                str = str.replaceAll("\\s+", " ");
            }
        }
        return str;
    }

    public String[] getSplitStudyWord() {
        if (StringUtils.hasText(studyTitle)) {
            return studyTitle.toLowerCase().split(" ");
        }
        return null;
    }
}
