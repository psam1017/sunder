package psam.portfolio.sunder.english.domain.academy.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Getter
@AllArgsConstructor(access = PRIVATE)
public class AcademyFullResponse {

    private UUID id;
    private String name;
    private String street;
    private String addressDetail;
    private String postalCode;
    private String phone;
    private String email;
    private boolean openToPublic;
    private AcademyStatus status;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;

    public static AcademyFullResponse from(Academy academy) {
        return AcademyFullResponse.builder()
                .id(academy.getUuid())
                .name(academy.getName())
                .street(academy.getAddress().getStreet())
                .addressDetail(academy.getAddress().getDetail())
                .postalCode(academy.getAddress().getPostalCode())
                .phone(academy.getPhone())
                .email(academy.getEmail())
                .openToPublic(academy.isOpenToPublic())
                .status(academy.getStatus())
                .createdDateTime(academy.getCreatedDateTime())
                .modifiedDateTime(academy.getModifiedDateTime())
                .build();
    }
}
