package psam.portfolio.sunder.english.domain.study.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDate;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

public class StudyStatisticResponse {

    @Getter @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class CountByStatus {

        private final StudyStatus status;
        private final Long count;
    }

    @Getter @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class CountByType {

        private final StudyType type;
        private final Long count;
    }

    @Getter @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class CountByClassification {

        private final StudyClassification classification;
        private final Long count;
    }

    @Getter @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class CountByTarget {

        private final StudyTarget target;
        private final Long count;
    }

    @Getter
    public static class CountByDay {

        @KoreanDate
        private LocalDate studyDate;
        private Long studyCount;
        private Long correctStudyWordCount;
        private Long totalStudyWordCount;

        public CountByDay(LocalDate studyDate, Long studyCount, Long correctStudyWordCount, Long totalStudyWordCount) {
            this.studyDate = studyDate;
            this.studyCount = studyCount;
            this.correctStudyWordCount = correctStudyWordCount;
            this.totalStudyWordCount = totalStudyWordCount;
        }

        @SuppressWarnings("unused")
        public CountByDay(String studyDate, Long studyCount, Long correctStudyWordCount, Long totalStudyWordCount) {
            this.studyDate = LocalDate.parse(studyDate);
            this.studyCount = studyCount;
            this.correctStudyWordCount = correctStudyWordCount;
            this.totalStudyWordCount = totalStudyWordCount;
        }
    }

    @Getter @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class TopStudent {

        private UUID studentId;
        private String studentName;
        private String schoolName;
        private Integer schoolGrade;
        private Double correctPercent;
        private Long studyCount;
        private Long studyWordCount;
    }

    @Getter @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class OldHomework {

        private UUID studyId;
        private String title;
        private StudyStatus status;
        @KoreanDateTime
        private LocalDateTime createdDateTime;
        private UUID studentId;
        private String studentName;
        private String schoolName;
        private Integer schoolGrade;
    }
}
