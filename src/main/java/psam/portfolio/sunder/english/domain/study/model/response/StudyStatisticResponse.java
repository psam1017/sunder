package psam.portfolio.sunder.english.domain.study.model.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class StudyStatisticResponse {

    @Getter @Setter @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class StudyCountByStatus {

        private Long assignedCount;
        private Long startedCount;
        private Long submittedCount;
    }

    @Getter @Setter @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class StudyCountByType {

        private Long tracingCount;
        private Long selectCount;
        private Long writingCount;
    }

    @Getter @Setter @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class StudyCountByClassification {

        private Long examCount;
        private Long practiceCount;
    }

    @Getter @Setter @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class StudyCountByTarget {

        private Long koreanCount;
        private Long englishCount;
    }

    @Getter @Setter @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class StudyCountByDay {

        private LocalDate studyDate;
        private Long studyCount;
        private Long correctCount;
        private Long totalCount;
    }

    @Getter @Setter @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class TopStudent {

        private UUID studentId;
        private String studentName;
        private String schoolName;
        private Integer schoolGrade;
        private Double correctPercent;
        private Long studyCount;
        private Long studyWordCount;
    }

    @Getter @Setter @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class OldHomework {

        private UUID studyId;
        private String title;
        private StudyStatus status;
        private LocalDateTime createdDateTime;
        private UUID studentId;
        private String studentName;
        private String schoolName;
        private Integer schoolGrade;
    }
}
