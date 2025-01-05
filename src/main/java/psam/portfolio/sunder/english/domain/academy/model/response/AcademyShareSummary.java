package psam.portfolio.sunder.english.domain.academy.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.AcademyShare;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Getter
@AllArgsConstructor(access = PRIVATE)
public class AcademyShareSummary {

    private UUID academyId;
    private String name;
    @KoreanDateTime
    private LocalDateTime createdDateTime;

    public static AcademyShareSummary from(AcademyShare academyShare) {
        return AcademyShareSummary.builder()
                .academyId(academyShare.getSharedAcademy().getId())
                .name(academyShare.getSharedAcademy().getName())
                .createdDateTime(academyShare.getCreatedDateTime())
                .build();
    }
}
