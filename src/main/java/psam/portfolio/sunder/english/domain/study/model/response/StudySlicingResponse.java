package psam.portfolio.sunder.english.domain.study.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.model.enumeration.StudyType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class StudySlicingResponse {

    private UUID id;
    private long sequence;
    private String title;
    private StudyStatus status;
    private StudyType type;
    private StudyClassification classification;
    private StudyTarget target;
    private LocalDateTime submitDateTime;
    private UUID studentId;
    private String attendanceId;
    private String studentName;
    private String schoolName;
    private Integer schoolGrade;
    private Long correctCount;
    private Long totalCount;
}
