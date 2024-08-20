package psam.portfolio.sunder.english.domain.study.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;

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
    @KoreanDateTime
    private LocalDateTime submitDateTime;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    private UUID studentId;
    private String attendanceId;
    private String studentName;
    private String schoolName;
    private Integer schoolGrade;
    private Integer correctCount;
    private Integer totalCount;
}
