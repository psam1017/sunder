package psam.portfolio.sunder.english.domain.study.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;
import psam.portfolio.sunder.english.domain.study.model.embeddable.StudyRange;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyFullResponse {

    private UUID id;
    private long sequence;
    private boolean ignoreCase;
    private String title;
    private StudyStatus status;
    private StudyType type;
    private StudyClassification classification;
    private StudyTarget target;
    @KoreanDateTime
    private LocalDateTime submitDateTime;
    private List<StudyRange> studyRanges; // 임베디드 타입

    public static StudyFullResponse from(Study study) {
        return StudyFullResponse.builder()
                .id(study.getId())
                .sequence(study.getSequence())
                .ignoreCase(study.isIgnoreCase())
                .title(study.getTitle())
                .status(study.getStatus())
                .type(study.getType())
                .classification(study.getClassification())
                .target(study.getTarget())
                .submitDateTime(study.getSubmitDateTime())
                .studyRanges(new ArrayList<>(study.getStudyRanges()))
                .build();
    }
}
